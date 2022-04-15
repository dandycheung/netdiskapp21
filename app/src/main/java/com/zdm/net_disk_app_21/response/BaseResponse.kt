package com.zdm.net_disk_app_21.response

/**
 * response class
 * 这里设置为open，方便子类继承
 */
open class BaseResponse(var msg:String, var responseCode:Int) {
    override fun toString(): String {
        return "BaseResponse(msg='$msg', responseCode=$responseCode)"
    }
}