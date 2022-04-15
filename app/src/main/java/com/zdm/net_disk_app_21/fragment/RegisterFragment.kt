package com.zdm.net_disk_app_21.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.entity.Account
import com.zdm.net_disk_app_21.response.RegisterResponse
import com.zdm.net_disk_app_21.service.UserService
import com.zdm.net_disk_app_21.util.RetrofitUtils
import com.zdm.net_disk_app_21.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    lateinit var navController: NavController

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = view?.let { Navigation.findNavController(it) }!!

        imageView_register_to_login.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }



        btn_register.setOnClickListener {
            val accountName = regUserEditText.text.toString()
            val password = regPwdEditText.text.toString()
            if (checkBox_isChecked.isChecked) {
                if (accountName.isNotEmpty() && password.isNotEmpty()) {
                    register(accountName, password)
                } else {
                    ToastUtil.showMsg(context, "请输入用户名和密码")
                }
            } else {
                ToastUtil.showMsg(context, "请勾选协议")
            }
        }
    }

    private fun register(accountName : String, password : String) {
        val retrofit = RetrofitUtils.getRetrofit()
        val userService = retrofit.create(UserService::class.java)
        val registerCall = userService.register(Account(accountName, password))
        registerCall.enqueue(object : Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.v("MyTag", "login failed!")
                Log.v("MyTag", "errMsg: $t")
            }
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.v("MyTag", response.body().toString())
                if(response.body()?.responseCode == 0) {
                    ToastUtil.showMsg(context, "注册成功，请登录")
                    navController.navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    ToastUtil.showMsg(context, "操作不合法或者用户名已注册")
                    Log.v("MyTag", "注册不合法")
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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}