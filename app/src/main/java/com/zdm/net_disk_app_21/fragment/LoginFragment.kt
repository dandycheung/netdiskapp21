package com.zdm.net_disk_app_21.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.zdm.net_disk_app_21.MainActivity
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.response.BaseResponse
import com.zdm.net_disk_app_21.service.UserService
import com.zdm.net_disk_app_21.util.RetrofitUtils
import com.zdm.net_disk_app_21.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    lateinit var navController : NavController
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = view?.let { Navigation.findNavController(it) }!!

        /**
         * 最好不用在布局文件中设置onClick属性的方式来监听 有时会找不到对应控制器中的逻辑方法
         */
        btn_forgetPwd.setOnClickListener{
            navController.navigate(R.id.action_loginFragment_to_updateAccountFragment)
        }

        btn_registerNow.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        btn_login.setOnClickListener {
            Log.v("MyTag", usernameEditText.text.toString())
            Log.v("MyTag", passwordEditText.text.toString())
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {

                login(username, password)
            } else {
                ToastUtil.showMsg(context, "请输入用户名和密码")
            }
        }
    }

    private fun login(username: String, password: String) {
        val retrofit = RetrofitUtils.getRetrofit()
        val userService = retrofit.create(UserService::class.java)
        val loginCall = userService.login(username,password)
        loginCall.enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.v("MyTag", "login failed!")
                Log.v("MyTag", "errMsg: $t")
            }
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                Log.v("MyTag", response.body().toString())
                if (response.body()?.responseCode == 0) {
                    ToastUtil.showMsg(context, "登陆成功")
                    val bundle = Bundle()
                    bundle.putString("username", username)
                    val intent = context?.let { Intent().setClass(it, MainActivity::class.java) }
                    intent?.putExtras(bundle)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    ToastUtil.showMsg(context, "账号或者密码错误")
                    Log.v("MyTag", "没有此用户")
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}