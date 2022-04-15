package com.zdm.net_disk_app_21.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.adapter.MyAdapter
import com.zdm.net_disk_app_21.customwidget.CustomDialog
import com.zdm.net_disk_app_21.entity.FileInfo
import com.zdm.net_disk_app_21.fileexplorer.FileExplorerActivity
import com.zdm.net_disk_app_21.filetransfer.FileTransferActivity
import com.zdm.net_disk_app_21.response.BaseResponse
import com.zdm.net_disk_app_21.service.FileService
import com.zdm.net_disk_app_21.util.FileUtils
import com.zdm.net_disk_app_21.util.RetrofitUtils
import com.zdm.net_disk_app_21.util.ToastUtil
import com.zdm.net_disk_app_21.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_files.*
import kotlinx.android.synthetic.main.layout_popup_window.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
const val REQUEST_PERMISSION_CODE = 1
class FilesFragment : Fragment() {
    // 根据官网查看的以activity为主的viewModel初始化方法
    private val fileViewModel : FileViewModel by activityViewModels()

    private lateinit var myAdapter : MyAdapter
    private lateinit var myAdapterWithCard : MyAdapter

    // 初始化防止没有网络导致获取不到数据，先实现测试数据
    private val file = FileInfo("test","2021-05-13 18:35",Date(),"3MB")

    /**
     *
     * logic of the fragment
     * this lifecycle is suitable to do this
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        // 获取登录界面传递的参数 —— 用户名
        val bundle = activity?.intent?.extras
        val username = bundle?.getString("username")

        // 没有数据时 不打印 开发测试时 直接进入主界面 没有数据传递
//        Log.v("parameter-test", username)

        // 没有网络时使用模拟静态数据 占位 防止adapter.getCount报空指针 返回数据长度时可能会传入null
        val files = listOf<FileInfo>(file)

        // 统一管理数据 把获取到的参数 用户名 交给 ViewModel进行管理
        if (username != null) {
            fileViewModel.setCurrentUser(username)
        }

        // 初始化适配器
        myAdapter = MyAdapter(false, fileViewModel)
        myAdapterWithCard = MyAdapter(true, fileViewModel)

        // 装配adapter的数据源
        myAdapter.setFiles(files)

        // 初始化 recyclerview
        recyclerView_files.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // 监听数据是否发送改动 显示用的文件列表
        fileViewModel.fileList.observe(viewLifecycleOwner, Observer {
            // 执行数据变动后的逻辑
//            Log.v("fileList.observe", it.toString())

            myAdapter.setFiles(it)
            myAdapterWithCard.setFiles(it)
            // 通知数据发生改变使得recyclerView刷新视图操作 放进观察者 ListAdapter好像可以有其他操作（不熟悉ListAdapter...）
            myAdapter.notifyDataSetChanged()
            myAdapterWithCard.notifyDataSetChanged()

            // 拿到数据刷新视图之后跳转 下拉刷新的状态
            swipeFileList.isRefreshing = false
        })

        // 下载文件列表观察
        fileViewModel.downloadList.observe(viewLifecycleOwner, Observer {
            myAdapter.setFileViewModel(fileViewModel)
            myAdapterWithCard.setFileViewModel(fileViewModel)
        })

        // popup window放在观察函数外 否则每一次数据变动引发的观察都会创建新的window导致 dismiss 操作的对象不对
        val windowView = layoutInflater.inflate(R.layout.layout_popup_window, null)
        var popupWindow = PopupWindow(windowView, ViewGroup.LayoutParams.MATCH_PARENT, 180) // 高度不要包裹内容 否则 无法覆盖底部导航条
        // popup window
        fileViewModel.usePopupWindow.observe(viewLifecycleOwner, Observer {
            Log.v("test-pop", it.toString())
            if (it) {
                popupWindow.showAtLocation(this.view, Gravity.BOTTOM, 0, 0)
            } else {
                popupWindow.dismiss()
            }
        })

        windowView.item_download.setOnClickListener {
            Log.v("downloadList", fileViewModel.downloadList.value.toString())
            val list = fileViewModel.downloadList.value
            if (fileViewModel.downloadList.value == null) {
                ToastUtil.showMsg(context, "请选择文件")
            } else {
                popupWindow.dismiss()
                if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_CODE)
                } else {
                    FileUtils.downloadProgress(context, list)
                }
                fileViewModel.getFileListFromServer()
                fileViewModel.setUsePop(false)
                myAdapter.setDownloadListTemp(mutableListOf())
            }
        }

        windowView.item_delete.setOnClickListener {
            val filename = fileViewModel.downloadList.value?.get(0)
            Log.v("delete-get-filename", filename)
            if (filename != null) {

                val dialog = context?.let { it1 -> CustomDialog(it1) }
                dialog?.setCancelable(false);
                dialog?.setTitle("是否删除")
                dialog?.setFilename(filename)

                // dialog?.editText_custom_dialog_filename?.visibility = View.GONE // 设置未生效

                dialog?.setCancel("取消"){d ->
                    d.dismiss()
                    popupWindow.dismiss()

                }
                dialog?.setConfirm("确定"){d ->
                    deleteFile(filename)
                    d.dismiss()
                    popupWindow.dismiss()
                    myAdapter.setDownloadListTemp(mutableListOf())
                    fileViewModel.getFileListFromServer()
                }?.show()



                // 清空下载列表 这里的逻辑是adapter中的数据做缓存 然后同步到viewModel 供其他页面操作 再通过adapter刷新数据同步到viewModel依此循环
                myAdapter.setDownloadListTemp(mutableListOf())
                fileViewModel.getFileListFromServer() // 刷新数据视图 视图有时刷新无效
            } else {
                ToastUtil.showMsg(context, "请选择文件")
            }
        }

        windowView.item_rename.setOnClickListener {
            val filename = fileViewModel.downloadList.value?.get(0)
            Log.v("rename-get-filename", filename)
            if (filename != null) {
                val dialog = context?.let { it1 -> CustomDialog(it1) }
                dialog?.setCancelable(false);
                dialog?.setTitle("重命名")
                dialog?.setFilename(filename)
                dialog?.setCancel("取消"){d ->
                    d.dismiss()
                    popupWindow.dismiss()
                    // window中的操作针对的都是操作列表（这里是下载列表作操作列表）中的数据 每次用完都必须更新重置
                    myAdapter.setDownloadListTemp(mutableListOf())
                    fileViewModel.getFileListFromServer()
                }
                dialog?.setConfirm("确定"){d ->
                    val newName = d.editText_custom_dialog_filename.text.toString()
                    FileUtils.renameFile(filename, newName)
                    d.dismiss()
                    popupWindow.dismiss()
                    myAdapter.setDownloadListTemp(mutableListOf())
                    fileViewModel.getFileListFromServer()
                }?.show()
            }
        }



        // 判断 没有文件列表就去服务器获取
         fileViewModel.fileList.value?:fileViewModel.getFileListFromServer()

        swipeFileList.setOnRefreshListener {
            fileViewModel.getFileListFromServer()
            fileViewModel.setUsePop(false)
        }

        btn_uploadFile.setOnClickListener {
            val intent = activity?.let { it1 -> Intent().setClass(it1.application, FileExplorerActivity::class.java) }
            startActivity(intent)
        }



        // switch model
//        switch_card.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                recyclerView_files.adapter = myAdapterWithCard
//
//            } else {
//                recyclerView_files.adapter = myAdapter
//
//            }
//        }

    }

    fun deleteFile(filename : String) {
        val retrofit = RetrofitUtils.getRetrofit()
        val fileService = retrofit.create(FileService::class.java)
        val deleteFileCall = fileService.deleteFile(filename)
        deleteFileCall.enqueue(object : Callback<BaseResponse>{
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.v("MyTag", "deleteFile failed...")
                Log.v("MyTag", t.toString())
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                Log.v("MyTag", "deleteFile succeed...")
                Log.v("MyTag", response.body().toString())
            }
        })
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_file_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                swipeFileList.isRefreshing = true

                Handler().postDelayed(Runnable {
                    fileViewModel.getFileListFromServer()
                    fileViewModel.setUsePop(false)
                }, 1000)
            }
            R.id.switch_model -> {
                ToastUtil.showMsg(context, "是否触发事件 by switch？")
                switchListener(R.id.switch_model)
            }
            R.id.file_transfer_list -> {
                val intent = activity?.let { Intent().setClass(it, FileTransferActivity::class.java) }
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun switchListener(switch: Int) {
        val switchModel = activity?.findViewById<Switch>(switch)
        switchModel?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                ToastUtil.showMsg(context, "switch")
                recyclerView_files.adapter = myAdapterWithCard
            } else {
                recyclerView_files.adapter = myAdapter
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showMsg(context, "已获取存储权限")
                } else {
                    ToastUtil.showMsg(context, "没有存储权限")
                }
            }
        }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}