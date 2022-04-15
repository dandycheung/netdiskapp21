package com.zdm.net_disk_app_21.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.zdm.net_disk_app_21.LoginActivity
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.response.UserInfoResponse
import com.zdm.net_disk_app_21.service.UserService
import com.zdm.net_disk_app_21.util.RetrofitUtils
import com.zdm.net_disk_app_21.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_mine.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MineFragment : Fragment() {
    private val fileViewModel: FileViewModel by activityViewModels()
    private val totalSpace = "/10GB"
    private val userSpaceUnit = "MB"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val currentUser = fileViewModel.currentUser.value

        Log.v("get-para-test", currentUser)

        textView_username.text = currentUser
        textView_user_space.text = "0" + "/0"

        if (currentUser != null) {
            if (currentUser.isNotEmpty()) {
//                Log.v("currentUser", currentUser)
                getUserInfoByAccountName(currentUser)
            }
        }

        imageView_power.setOnClickListener {
            val intent = activity?.let { it1 -> Intent().setClass(it1, LoginActivity::class.java) }
            startActivity(intent)
            // 跳转之后finish防止按下系统返回键 又回到此页面（好耶！）
            activity?.finish()
        }
    }

    private fun getUserInfoByAccountName(username : String) {
        Log.v("username", username)
        val retrofit = RetrofitUtils.getRetrofit()
        val userService = retrofit.create(UserService::class.java)
        val userInfoCall = userService.getUserInfo(username)
        userInfoCall.enqueue(object : Callback<UserInfoResponse>{
            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.v("getUserInfo-fail", t.toString())
            }
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                Log.v("getUserInfo-success", response.body()?.userInfo.toString())
                 textView_username.text = response.body()?.userInfo?.username
                 textView_user_space.text = response.body()?.userInfo?.userSpace + userSpaceUnit + totalSpace
            }
        })
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}