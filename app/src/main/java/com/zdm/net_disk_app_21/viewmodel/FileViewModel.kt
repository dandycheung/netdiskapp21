package com.zdm.net_disk_app_21.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zdm.net_disk_app_21.entity.FileInfo
import com.zdm.net_disk_app_21.response.FileResponse
import com.zdm.net_disk_app_21.service.FileService
import com.zdm.net_disk_app_21.util.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FileViewModel(application: Application) : AndroidViewModel(application) {
    private var _fileList = MutableLiveData<List<FileInfo>>()
    private var _downloadList = MutableLiveData<List<String>>()
    private var _currentUser = MutableLiveData<String>()
    private var _usePopupWindow = MutableLiveData<Boolean>()

    val fileList : LiveData<List<FileInfo>>
        get() = _fileList
    val downloadList : LiveData<List<String>>
        get() = _downloadList
    val usePopupWindow : LiveData<Boolean>
        get() = _usePopupWindow
    val currentUser : LiveData<String>
        get() = _currentUser

    init {
        _usePopupWindow.value = false
        _currentUser.value = "请登录"
    }

    // 不会对MutableLiveData<List<String>>()类型进行数据追加的另一种写法
    fun setDownloadList(list : List<String>) {
        _downloadList.value = list
    }

    fun setUsePop(flag : Boolean) {
        _usePopupWindow.value = flag
    }

    fun setCurrentUser(username:String) {
        _currentUser.value = username
    }

    fun getFileListFromServer() {
        val retrofit = RetrofitUtils.getRetrofit()
        val fileService = retrofit.create(FileService::class.java)
        val fileListCall = fileService.getFileList()
        Log.v("MyTag", "ready to get fileList...")
        fileListCall.enqueue(object : Callback<FileResponse> {
            override fun onFailure(call: Call<FileResponse>, t: Throwable) {
                Log.v("MyTag", "get fileList failed...")
                Log.v("MyTag", "error: $t")
            }
            override fun onResponse(call: Call<FileResponse>, response: Response<FileResponse>) {
                Log.v("MyTag", "get fileList succeed...")

                _fileList.value = response.body()?.data
            }
        })

    }
}