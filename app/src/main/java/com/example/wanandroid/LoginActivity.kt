package com.example.wanandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.wanandroid.databinding.ActivityLoginBinding
import com.example.wanandroid.model.UiState
import com.example.wanandroid.viewmodel.LoginViewModel
import com.example.wanandroid.widget.LoadingDialog
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var loadingDialog: LoadingDialog? = null

    // 记录当前是登录模式还是注册模式
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initObserver()
    }

    private fun initView() {
        // 切换模式按钮点击事件
        binding.tvGoRegister.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUiMode()
        }

        // 登录/注册按钮点击事件
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (isLoginMode) {
                viewModel.login(username, password)
            } else {
                // 这里为了简单，把确认密码也当做 password 传过去，实际开发中建议多加一个输入框
                viewModel.register(username, password, password)
            }
        }
    }

    private fun updateUiMode() {
        if (isLoginMode) {
            binding.tvTitle.text = "欢迎来到玩安卓"
            binding.btnLogin.text = "登 录"
            binding.tvGoRegister.text = "还没有账号？去注册"
        } else {
            binding.tvTitle.text = "注册新账号"
            binding.btnLogin.text = "注 册"
            binding.tvGoRegister.text = "已有账号？去登录"
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            // repeatOnLifecycle 保证只有在页面可见时才收集数据
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> {
                            hideLoading()
                            val msg = if (isLoginMode) "登录成功" else "注册成功"
                            Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                            // 登录成功后，关闭页面返回到来源页
                            finish()
                        }
                        is UiState.Error -> {
                            hideLoading()
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    private fun hideLoading() {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}