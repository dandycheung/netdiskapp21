package com.zdm.net_disk_app_21.download;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用于实现下载文件带进度条 - 组件1
 */
public class DownloadCallback implements Callback {
    public static final int DOWNLOAD_START = 0;
    public static final int DOWNLOAD_PROCESS = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DOWNLOAD_FAILED = 3;

    private File file;
    private OnDownloadListener listener;

    public DownloadCallback(File file, OnDownloadListener listener) {
        this.file = file;
        this.listener = listener;
    }

    public DownloadCallback(String destPath, OnDownloadListener listener) {
        this.file = new File(destPath);
        this.listener = listener;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_START:
                    listener.onDownloadStart();
                    break;
                case DOWNLOAD_PROCESS:
                    listener.onDownloading(msg.arg1);
                    break;
                case DOWNLOAD_COMPLETE:
                    listener.onDownloadSuccess();
                    break;
                case DOWNLOAD_FAILED:
                    listener.onDownloadFailed((Exception) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void sendMessage(int what, int progress, Object object) {
        Message obtain = Message.obtain();
        obtain.what = what;
        obtain.arg1 = progress;
        obtain.obj = object;
        mHandler.sendMessage(obtain);
    }
    @Override
    public void onFailure(Call call, IOException e) {
        sendMessage(DOWNLOAD_FAILED, 0, e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            sendMessage(DOWNLOAD_FAILED, 0, new RuntimeException("response error"));
            return;
        }
        sendMessage(DOWNLOAD_START, 0, null);

        InputStream is = null;
        FileOutputStream fos = null;
        byte[] buf = new byte[2048];
        int len = 0;
        try {
            is = response.body().byteStream();
            fos = new FileOutputStream(file);

            // core code
            long total = response.body().contentLength();
            long sum = 0;
            int progress = 0;
            int temp;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                temp = (int) (sum * 1.0f / total * 100);
                if (temp > progress) {
                    progress = temp;
                    sendMessage(DOWNLOAD_PROCESS, progress, null);
                }
            }
            fos.flush();

            sendMessage(DOWNLOAD_COMPLETE, 100, null);
        } catch (Exception e) {
            sendMessage(DOWNLOAD_FAILED, 0, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                sendMessage(DOWNLOAD_FAILED, 0, e);
            }
        }
    }

    public interface OnDownloadListener {
        void onDownloadStart();
        void onDownloading(int progress);
        void onDownloadSuccess();
        void onDownloadFailed(Exception e);
    }
}
