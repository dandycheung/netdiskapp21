package com.zdm.net_disk_app_21.response;

import java.io.FileInputStream;

public class FileInputStreamResponse {
    private String msg;
    private int responseCode; // 程序自定义的应用级的响应码
    private FileInputStream data;

    public FileInputStreamResponse(String msg, int responseCode, FileInputStream data) {
        this.msg = msg;
        this.responseCode = responseCode;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public FileInputStream getData() {
        return data;
    }

    public void setData(FileInputStream data) {
        this.data = data;
    }
}
