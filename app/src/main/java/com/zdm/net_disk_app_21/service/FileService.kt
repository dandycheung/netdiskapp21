package com.zdm.net_disk_app_21.service

import com.zdm.net_disk_app_21.response.BaseResponse
import com.zdm.net_disk_app_21.response.FileResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FileService {
    /**
     * 1. 通用上传 单/多文件即可
     * 注意
     *  @Part 注解修饰 MultipartBody.Part 参数类型，时可以不指定值
     *  但是 修饰 MultipartBody 这类其他类型时 must supply a name，即需要我们指定一个值 如 @Part("files")
     *      且服务器端接收时 @RequestPart("files") 注解的值也必须是 files，客户端和服务器端必须对应才能正常运行
     *          - 客户端和服务器端相互配合，形成一个完整的流程
     */
    @POST("/netdisk/files/upload")
    @Multipart
    fun uploadCommonFile(@Part("files") multipartBody: MultipartBody, @Query("fileNameList") fileNameList:List<String>) : Call<BaseResponse>

    /**
     * 2. 文件下载 下载服务器的静态资源
     *  1. 方法使用get注解
     *  2. 参数使用@Url注解
     *  3. 返回值使用okhttp3自带的 ResponseBody 类型，
     * 注：此接口服务器端没有对应接口，这里只需要能访问到服务器的资源就可以了
     *  所以使用Url去获取我们需要的资源
     */
    @GET
    @Streaming // 支持大文件下载（不读进内存直接写入硬盘）
    fun downloadSingleFile(@Url url : String) : Call<ResponseBody>

    /**
     * 3. get file list
     * 使用String接收数据
     */
    @GET("/netdisk/files/list")
    fun getFileList() : Call<FileResponse>

    @GET("/netdisk/files/delete")
    fun deleteFile(@Query("filename") filename : String) : Call<BaseResponse>

    @POST("/netdisk/files/rename")
    fun renameFile(@Query("oldFileName") oldFileName : String, @Query("newFileName") newFileName : String) : Call<BaseResponse>

    // java后台输入流转json失败弃用此方案
//    @GET("/netdisk/files/download")
//    fun downloadFile(@Query("fileName") fileName: String) : Call<FileInputStreamResponse>
}