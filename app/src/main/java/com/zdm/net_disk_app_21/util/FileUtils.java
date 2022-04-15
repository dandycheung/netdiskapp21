package com.zdm.net_disk_app_21.util;

import android.content.Context;
import android.util.Log;

import com.zdm.net_disk_app_21.download.DownloadCallback;
import com.zdm.net_disk_app_21.download.OkHttpManager;
import com.zdm.net_disk_app_21.response.BaseResponse;
import com.zdm.net_disk_app_21.service.FileService;
import com.zdm.net_disk_app_21.viewmodel.TransferFileViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileUtils {
    private final static Retrofit retrofit = RetrofitUtils.getRetrofit();
    private static TransferFileViewModel viewModel;

    public void setViewModel(TransferFileViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public static boolean saveFile(File file, InputStream inputStream) throws IOException {

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fileOutputStream = null;
        boolean flag = false;
        try {
            fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
            }
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static void uploadCommonFile(String dir, List<String> fileNameList) {
        FileService fileService = retrofit.create(FileService.class);

        MultipartBody.Builder builder = new MultipartBody.Builder();

        Log.v("dirTest", dir);

        for (String fileName : fileNameList) {
            Log.v("fileNameListTest", fileName);

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/from-data"),
                    new File(dir, fileName));
            builder.addFormDataPart("files", fileName, requestBody);
        }

        MultipartBody multipartBody = builder.build();

        Call<BaseResponse> uploadCommonFileCall = fileService.uploadCommonFile(multipartBody, fileNameList);

        uploadCommonFileCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.v("MyTag", "uploading...");
                Log.v("MyTag", response.body().toString());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.v("MyTag", "test failed...");
                Log.v("MyTag", t.toString());
            }
        });

    }

    public static void downloadFile(Context context, List<String> filenames) {
        // 这里单开一个 retrofit 主要是另起线程 下载大文件 回调会在子线程处理
        Retrofit retrofitForDownload = new Retrofit.Builder()
                .baseUrl("http://10.28.202.131")
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor()) // 子线程处请求回调会导致在回调中无法处理主线程的数据
                .build();

        FileService fileService = retrofitForDownload.create(FileService.class);

        for (String fileName : filenames) {
            Log.v("download-filename", fileName);
            Call<ResponseBody> downloadFileCall = fileService.downloadSingleFile(RetrofitUtils.BASE_URL + RetrofitUtils.BASE_PATH + fileName);

            Log.v("download-url", RetrofitUtils.BASE_URL + RetrofitUtils.BASE_PATH + fileName);

            downloadFileCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.v("MyTag", "downloading...");
                    // api 29 之后 android 建议每个app使用自己的文件存储数据
                    Log.v("MyTag", context.getExternalFilesDir("").toString());
                    try {
                        FileUtils.saveFile(new File(context.getExternalFilesDir("zdm"), fileName), response.body().byteStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("MyTag", "test failed...");
                    Log.v("MyTag", t.toString());
                }
            });
        }
    }

    public static void downloadProgress(Context context, List<String> filenames) {
        for (String fileName : filenames) {
            Log.v("download-filename", fileName);
            OkHttpManager.getInstance().download(RetrofitUtils.BASE_URL + RetrofitUtils.BASE_PATH + fileName,
                    new File(context.getExternalFilesDir("zdm"), fileName), new DownloadCallback.OnDownloadListener() {
                        @Override
                        public void onDownloadStart() {
                            Log.v("MyTag", "onDownloadStart...");
                        }

                        @Override
                        public void onDownloading(int progress) {
                            Log.v("MyTag", "onDownloading: " + progress);
//                            progressBar.setProgress(progress);
                           //  viewModel.setProgress(progress); // 跨activity了 又不会 activity之间 传递动态数据
                            // 把传输列表做成容器类型的 方案一
                        }

                        @Override
                        public void onDownloadSuccess() {

                            Log.v("MyTag", "onDownloadSuccess...");
                        }

                        @Override
                        public void onDownloadFailed(Exception e) {
                            Log.v("MyTag", "onDownloadFailed...");
                        }
                    });
        }
    }

    public static File[] getFileExplorer(File currentParent) {
        File[] currentFiles = currentParent.listFiles();
        return currentFiles;
    }

    public static void renameFile(String oldFileName, String newFileName) {
        FileService fileService = retrofit.create(FileService.class);
        Call<BaseResponse> renameFileCall = fileService.renameFile(oldFileName, newFileName);
        renameFileCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.v("MyTag", "renameFile succeed...");
                Log.v("MyTag", response.body().toString());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.v("MyTag", "renameFile failed...");
                Log.v("MyTag", t.toString());
            }
        });
    }
}
