package com.zdm.net_disk_app_21

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navControl : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 可直接通过id使用控件
//        val bottomNavigationView = this.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 控制器需要初始化
        navControl = findNavController(R.id.fragment)

        val appBarConfiguration = AppBarConfiguration.Builder(bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(this, navControl, appBarConfiguration)
        NavigationUI.setupWithNavController(bottomNavigationView, navControl)

        // 实现底部导航时 不要 actionbar? how
//        bottomNavigationView.menu.
    }
}