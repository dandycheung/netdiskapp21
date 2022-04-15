package com.zdm.net_disk_app_21.response

import com.zdm.net_disk_app_21.entity.Account

/**
 * response for register
 * 注：服务器端这里定义的是 AddUserResponse，这个只是习惯问题，实际逻辑是一致的
 */
class RegisterResponse(msg: String, responseCode: Int, var account: Account) :
    BaseResponse(msg, responseCode) {

}