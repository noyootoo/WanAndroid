package com.example.wanandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.adapter.ArticleAdapter
import com.example.wanandroid.databinding.ActivityMainBinding
import com.example.wanandroid.model.Article
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val systemFragment = SystemFragment()
    private val mineFragment = MineFragment()
    private val searchFragment = SearchFragment()
    private var activeFragment: Fragment = homeFragment
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // 1. 生命周期打印
        Log.d("MainActivity_Life", "onCreate: 页面创建")

        enableEdgeToEdge()
        // 2. 初始化 MainActivity 的 Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)

        // 🔴 3. 极其重要：把整个界面设置到屏幕上！
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ================= 在这里往下写我们的业务逻辑 =================

        // ========== 把 Fragment 放到占位容器中 ==========
        // 判断 savedInstanceState == null 是为了防止屏幕旋转时重复添加
        // 首次进入页面时初始化 Fragment，避免配置变化后重复添加
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragmentContainer, mineFragment).hide(mineFragment)
                add(R.id.fragmentContainer, systemFragment).hide(systemFragment)
                add(R.id.fragmentContainer, searchFragment).hide(searchFragment)
                add(R.id.fragmentContainer, homeFragment) // 首页不隐藏
                commit()
            }
        }
        binding.bottomNav.setOnItemSelectedListener { item ->// 监听底部导航点击事件，切换到对应的页面
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(homeFragment)
                    true // 返回 true 表示我们处理了这个点击事件，按钮可以变色
                }

                R.id.nav_system -> {
                    switchFragment(systemFragment)
                    true
                }

                R.id.nav_mine -> {
                    switchFragment(mineFragment)
                    true
                }
                R.id.nav_search -> {
                    switchFragment(searchFragment)
                    true
                }

                else -> false
            }
        }


    }

    /**
     * 切换当前显示的 Fragment。
     * 通过 hide/show 的方式保留各页面状态，避免重复创建。
     *
     * @param targetFragment 需要显示的目标 Fragment
     */
    private fun switchFragment(targetFragment: Fragment) {
        if (activeFragment == targetFragment) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(targetFragment)
            .commit()

        activeFragment = targetFragment
    }

}