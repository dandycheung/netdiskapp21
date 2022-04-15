package com.zdm.net_disk_app_21.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.entity.Account
import com.zdm.net_disk_app_21.response.BaseResponse
import com.zdm.net_disk_app_21.service.UserService
import com.zdm.net_disk_app_21.util.RetrofitUtils
import com.zdm.net_disk_app_21.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_update_account.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateAccountFragment : Fragment() {
    lateinit var navController: NavController

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = view?.let { Navigation.findNavController(it) }!!

        imageView_update_to_login.setOnClickListener {
            navController.navigate(R.id.action_updateAccountFragment_to_loginFragment)
        }

        button_confirm.setOnClickListener {
            val accountName = edit_account.text.toString()
            val password = edit_password.text.toString()

            if (accountName.isNotEmpty() && password.isNotEmpty()) {

                updatePassword(accountName, password)
            } else {
                ToastUtil.showMsg(context, "请输入用户名和新密码")
            }

        }
    }

    private fun updatePassword(accountName: String, password: String) {
        val retrofit = RetrofitUtils.getRetrofit()
        val userService = retrofit.create(UserService::class.java)
        val updateCall = userService.update(Account(accountName, password))
        updateCall.enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.v("MyTag", "update failed!")
                Log.v("MyTag", "errMsg: $t")
            }
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                Log.v("MyTag", response.body().toString())

                if(response.body()?.responseCode == 0) {
                    ToastUtil.showMsg(context, "修改请求已发出，请稍后登录")
                    navController.navigate(R.id.action_updateAccountFragment_to_loginFragment)
                } else {
                    ToastUtil.showMsg(context, "操作不合法")
                    Log.v("MyTag", "update不合法")
                }
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
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpdateAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}