package com.zdm.net_disk_app_21.response

import com.zdm.net_disk_app_21.entity.UserInfo

class UserInfoResponse(msg: String, responseCode: Int, val userInfo : UserInfo) : BaseResponse(msg, responseCode) {

}