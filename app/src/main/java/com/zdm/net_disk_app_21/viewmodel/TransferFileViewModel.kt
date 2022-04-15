package com.zdm.net_disk_app_21.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TransferFileViewModel(application: Application) : AndroidViewModel(application) {
    private var _transferList = MutableLiveData<List<String>>()
    private var _progress = MutableLiveData<Int>()

    val transferList : LiveData<List<String>>
        get() = _transferList
    val progress : LiveData<Int>
        get() = _progress

    init {
        _transferList.value = mutableListOf<String>("没有传输中的文件")
    }

    fun setProgress(progress : Int) {
        _progress.value = progress
    }

    fun delete(fileneme : String) {
        _transferList.value
    }
}