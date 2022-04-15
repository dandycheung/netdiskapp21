package com.zdm.net_disk_app_21.download;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 用于实现下载文件带进度条 - 组件2
 */
public class OkHttpManager {
    private static OkHttpManager mInstance;
    private OkHttpClient mOkHttpClient;
    public static OkHttpManager getInstance() {
        if (null == mInstance) {
            synchronized (OkHttpManager.class) {
                if (null == mInstance) {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;
    }

    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void enqueue(String url, Object requestBody, Callback callBack) {
        String jsonRequest = new Gson().toJson(requestBody);
        RequestBody create = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest);
        Request request = new Request.Builder().url(url).post(create).build();
        mOkHttpClient.newCall(request).enqueue(callBack);
    }

    public void uploadFile(final String url, final File file, Callback callback) {
        MultipartBody multipartBody = new MultipartBody.Builder()
                .addFormDataPart("file","",RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();

        Request request = new Request.Builder().url(url).post(multipartBody).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public void uploadFiles(final String url, List<File> files, Callback callback) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            RequestBody requestBody = MultipartBody.create(MediaType.parse("multipart/form-data"), file);
            multipartBuilder.addFormDataPart("myfiles","",requestBody);
        }
        RequestBody requestBody = multipartBuilder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public Call download(final String url, final File file, final com.zdm.net_disk_app_21.download.DownloadCallback.OnDownloadListener listener) {
        Call call = null;
        try {
            Request request = new Request.Builder().url(url).build();
            call = mOkHttpClient.newCall(request);
            call.enqueue(new com.zdm.net_disk_app_21.download.DownloadCallback(file, listener));
        } catch (Exception e) {
            if(listener != null){
                listener.onDownloadFailed(e);
            }
        }
        return call;

    }
}
