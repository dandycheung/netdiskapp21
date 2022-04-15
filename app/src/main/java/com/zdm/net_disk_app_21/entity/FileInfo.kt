package com.zdm.net_disk_app_21.entity

import java.util.*

/**
 * 使用data kotlin会自动帮我们创建域 getter setter
 */
data class FileInfo (
    val fileName:String,
    val createTime : String, // 这里仅用作接收服务器数据进行显示到TextView 注意 属性名称/K以及服务器端对应类型必须一致
    val lastEditTime : Date, // 这里仅用作接收服务器数据进行显示到TextView
    val fileSize:String
        )