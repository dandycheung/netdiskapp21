package com.zdm.net_disk_app_21.fileexplorer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zdm.net_disk_app_21.MainActivity
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.adapter.FileExplorerAdapter
import com.zdm.net_disk_app_21.util.FileUtils
import com.zdm.net_disk_app_21.util.ToastUtil
import com.zdm.net_disk_app_21.viewmodel.FileExplorerViewModel
import kotlinx.android.synthetic.main.activity_file_explorer.*
import java.io.File

const val REQUEST_PERMISSION_CODE = 1

class FileExplorerActivity : AppCompatActivity() {
    lateinit var root : File
    lateinit var currentParent : File
    lateinit var currentFiles : Array<File>

    var fileNameList = mutableListOf<String>()

    private var uploadNum = 0

    private lateinit var fileExplorerViewModel: FileExplorerViewModel
    private lateinit var fileExplorerAdapter: FileExplorerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_explorer)

        // 初始化文件管理器的初始信息
        initFileExplorerInfo()

        fileExplorerViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(FileExplorerViewModel::class.java)

        // 获取文件管理器的初始信息 进行装配、加工 作为 adapter 的 ds
        fileExplorerViewModel.getFileExplorer(currentFiles)

        Log.v("Tag_ac_cu", fileExplorerViewModel.currentFiles.value.toString())

        fileExplorerAdapter = FileExplorerAdapter(fileExplorerViewModel.currentFiles.value)

        recyclerView_explorer.apply {
            adapter = fileExplorerAdapter
            layoutManager = LinearLayoutManager(context)
        }

        fileExplorerViewModel.currentFiles.observe(this, Observer {
            Log.v("Tag_currentFiles: ", it.toString())
            fileExplorerAdapter.setList(it)
            fileExplorerAdapter.notifyDataSetChanged()


            textView_file_path.text = "当前路径" + currentParent.absolutePath
        })

        // 尝试传递 checkBox过来 进行相应的逻辑处理 ，但是这里是点击item而不是 勾选（就蛮尴尬）
        // 虽然这里可以进行 checkBox 被选中的逻辑，但是并不是 勾选的逻辑 难受
        // 下面进行取巧操作 点击item时判断是否勾选 勾选则创建 文件路径 来进行读取文件并上传
        fileExplorerAdapter.setOnItemClickListener { parent, itemView, checkBox, position, data ->
            if (checkBox.isChecked) {
                ToastUtil.showMsg(applicationContext, "已勾选")
                // 创建上传文件路径
                fileNameList.add(data)
                textView_upload_num.text = (++uploadNum).toString()
            }

            if (!checkBox.isChecked) {
                ToastUtil.showMsg(applicationContext, "是否触发取消勾选的事件")
                if (uploadNum > 0)
                    uploadNum--
            }

            val listFilesTemp = currentFiles[position].listFiles()
            if (listFilesTemp == null || listFilesTemp.isEmpty()) {
                if (!checkBox.isChecked) {
                    ToastUtil.showMsg(applicationContext, "需要上传请勾选")
                }
            } else {
                currentParent = currentFiles[position]
                currentFiles = listFilesTemp
                fileExplorerViewModel.getFileExplorer(currentFiles)
            }
        }

        imageView_btn_back.setOnClickListener {
            if (currentParent.absolutePath.equals(root.absolutePath)) {
                finish()
            } else {
                currentParent = currentParent.parentFile
                currentFiles = currentParent.listFiles()
                fileExplorerViewModel.getFileExplorer(currentFiles)
            }
        }

        button_upload.setOnClickListener{
            if (textView_upload_num.text.equals("")) {
                ToastUtil.showMsg(applicationContext, "请选择文件")
            } else {
                FileUtils.uploadCommonFile(currentParent.absolutePath, fileNameList)
                uploadNum = 0
                textView_upload_num.text = ""
                val intent = Intent().setClass(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }

    /**
     * 初始化
     *  1. 根目录 root
     *  2. 当前父目录 currentParent
     *  3. 当前父目录下的子项 currentFiles
     */
    private fun initFileExplorerInfo() {
        val isLoadSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
        if (Build.VERSION.SDK_INT < 29 || ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE)

        }
        if (isLoadSDCard) {
            root = Environment.getExternalStorageDirectory()
            Log.v("root: ", root.toString())
            currentParent = root
            Log.v("currentParent: ", currentParent.toString())
            currentFiles = FileUtils.getFileExplorer(currentParent)
            Log.v("currentFiles: ", currentFiles.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 根据返回结果进行不同的处理
        // when 即 kotlin 的条件选择语句
        when(requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "已获取读取权限", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "没有读取权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}