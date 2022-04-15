package com.zdm.net_disk_app_21.util;

import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 获取网络请求工具 retrofit
 * 服务器：39.99.253.162
 */
public class RetrofitUtils {
    public static String BASE_URL = "http://192.168.1.3";
    public static String BASE_PATH = "/netdisk/files/";
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(
                        new OkHttpClient.Builder()
                                .addInterceptor(
                                        new LoggingInterceptor.Builder()
                                                .setLevel(Level.BASIC)
                                                .loggable(true)
                                                .build()
                                )
                                .build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .callbackExecutor(Executors.newSingleThreadExecutor()) // 子线程处请求回调会导致在回调中无法处理主线程的数据
                .build();
    }
}
