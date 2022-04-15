package com.zdm.net_disk_app_21.filetransfer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.zdm.net_disk_app_21.R
import com.zdm.net_disk_app_21.fragment.TransferDoneListFragment
import com.zdm.net_disk_app_21.fragment.TransferingListFragment
import kotlinx.android.synthetic.main.activity_file_transfer.*

class FileTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_transfer)

        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }
            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> TransferingListFragment()
                    else -> TransferDoneListFragment()
                }
            }
        }

        // kotlin中最后一个参数（接口）使用lambda实现允许写在参数括号外界
        TabLayoutMediator(tabLayout, viewPager2){tab, position ->
            when(position) {
                0 -> {
                    tab.text = "传输中"
                }
                else -> tab.text = "已完成"
            }
        }.attach()

        imageView_file_transfer_list_to_main.setOnClickListener {
            finish()
        }
    }
}