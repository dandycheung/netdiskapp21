package com.zdm.net_disk_app_21

import android.util.Log
import com.zdm.net_disk_app_21.entity.FileInfo
import com.zdm.net_disk_app_21.service.FileService
import com.zdm.net_disk_app_21.util.RetrofitUtils
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGetFileList() {
        val retrofit = RetrofitUtils.getRetrofit()
        val fileService = retrofit.create(FileService::class.java)
        val fileListCall = fileService.getFileList()

        println("ready to request....")
        fileListCall.enqueue(object : Callback<List<FileInfo>>{
            override fun onFailure(call: Call<List<FileInfo>>, t: Throwable) {
                println("request failed...")
                Log.v("MyTag", "getFileList failed!")
                Log.v("MyTag", "errMsg: $t")
            }

            override fun onResponse(call: Call<List<FileInfo>>, response: Response<List<FileInfo>>) {
                println("request succeed...")
                Log.v("MyTag", response.body().toString())
                println(response.body())
            }
        })
    }
}