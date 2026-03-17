package com.example.wanandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanandroid.model.UiState
import com.example.wanandroid.model.UserInfo
import com.example.wanandroid.repository.ApiException
import com.example.wanandroid.repository.NetworkException
import com.example.wanandroid.repository.ParseException
import com.example.wanandroid.repository.UserRepository
import com.example.wanandroid.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _loginState = MutableSharedFlow<UiState<UserInfo>>()
    val loginState: SharedFlow<UiState<UserInfo>> = _loginState.asSharedFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            emitError("账号或密码不能为空")
            return
        }

        viewModelScope.launch {
            _loginState.emit(UiState.Loading)
            repository.login(username, password).fold(
                onSuccess = { userInfo ->
                    DataStoreManager.saveUsername(userInfo.username)
                    _loginState.emit(UiState.Success(userInfo))
                },
                onFailure = { exception ->
                    handleException(exception)
                }
            )
        }
    }

    fun register(username: String, password: String, repassword: String) {
        if (username.isBlank() || password.isBlank() || repassword.isBlank()) {
            emitError("输入不能为空")
            return
        }
        if (password != repassword) {
            emitError("两次密码输入不一致")
            return
        }

        viewModelScope.launch {
            _loginState.emit(UiState.Loading)
            repository.register(username, password, repassword).fold(
                onSuccess = { userInfo ->
                    DataStoreManager.saveUsername(userInfo.username)
                    _loginState.emit(UiState.Success(userInfo))
                },
                onFailure = { exception ->
                    handleException(exception)
                }
            )
        }
    }

    private suspend fun handleException(exception: Throwable) {
        val errorMsg = when (exception) {
            is NetworkException -> exception.message ?: "网络开小差了"
            is ApiException -> exception.message ?: "服务器傲娇了"
            is ParseException -> exception.message ?: "数据解析出错了"
            else -> "未知错误：${exception.message}"
        }
        _loginState.emit(UiState.Error(errorMsg))
    }

    private fun emitError(msg: String) {
        viewModelScope.launch {
            _loginState.emit(UiState.Error(msg))
        }
    }
}