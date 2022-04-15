package com.zdm.net_disk_app_21.service

import com.zdm.net_disk_app_21.response.BaseResponse
import com.zdm.net_disk_app_21.response.RegisterResponse
import com.zdm.net_disk_app_21.entity.Account
import com.zdm.net_disk_app_21.response.UserInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("/netdisk/user/login")
    fun login(@Query("username") username:String, @Query("password") password:String) : Call<BaseResponse>

    /**
     * 注册账号 账号和用户信息作分离
     */
    @POST("/netdisk/user/register")
    fun register(@Body account: Account) : Call<RegisterResponse>

    @POST("/netdisk/user/update")
    fun update(@Body account: Account) : Call<BaseResponse>


    @GET("/netdisk/user/info")
    fun getUserInfo(@Query("username") username:String) : Call<UserInfoResponse>
}