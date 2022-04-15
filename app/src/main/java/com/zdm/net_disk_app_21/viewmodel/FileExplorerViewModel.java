package com.zdm.net_disk_app_21.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zdm.net_disk_app_21.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileExplorerViewModel extends AndroidViewModel {

    private MutableLiveData<List<Map<String, Object>>> _currentFiles = new MutableLiveData<>();
    public LiveData<List<Map<String, Object>>> currentFiles;

    private MutableLiveData<List<String>> _fileNameList = new MutableLiveData<>();
    public LiveData<List<String>> fileNameList;

    public FileExplorerViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void set_fileNameList(MutableLiveData<List<String>> _fileNameList) {
        this._fileNameList = _fileNameList;
    }

    public LiveData<List<String>> getFileNameList() {
        fileNameList = _fileNameList;
        return fileNameList;
    }

    // 需要进行LiveData版本值得设置 这里setter比较特殊 通过getFileExplorer赋值
    public void setCurrentFiles() {
        this.currentFiles = _currentFiles;
    }

    public LiveData<List<Map<String, Object>>> getCurrentFiles() {
        currentFiles = _currentFiles;
        return currentFiles;
    }

    public void getFileExplorer(File[] currentFiles) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (File file : currentFiles) {
            Log.v("Tag_file", file.toString());
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", file.getName());
            if (file.isFile()) {
                map.put("icon", R.drawable.ic_file_24);
            } else {
                map.put("icon", R.drawable.ic_folder_24);
            }
            list.add(map);
        }
        _currentFiles.setValue(list);
        // java中需要自己设置更新数据
        setCurrentFiles();

    }

    public void addFiles(String fileName) {
        _fileNameList.getValue().add(fileName);

    }
}
