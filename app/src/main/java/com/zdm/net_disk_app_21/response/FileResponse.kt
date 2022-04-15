package com.zdm.net_disk_app_21.response

import com.zdm.net_disk_app_21.entity.FileInfo

class FileResponse (val msg:String, val responseCode:Int, val data:List<FileInfo>) {
}