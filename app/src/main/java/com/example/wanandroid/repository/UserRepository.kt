package com.example.wanandroid.repository

import com.example.wanandroid.model.UserInfo
import com.example.wanandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import com.google.gson.JsonSyntaxException

class UserRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun login(username: String, password: String): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(username, password)
                if (response.errorCode == 0 && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.errorMsg ?: "登录失败"))
                }
            } catch (e: Exception) {
                Result.failure(handleException(e))
            }
        }
    }

    suspend fun register(username: String, password: String, repassword: String): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(username, password, repassword)
                if (response.errorCode == 0 && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.errorMsg ?: "注册失败"))
                }
            } catch (e: Exception) {
                Result.failure(handleException(e))
            }
        }
    }

    private fun handleException(e: Exception): Exception {
        return when (e) {
            is SocketTimeoutException -> NetworkException("网络超时，请稍后重试")
            is IOException -> NetworkException("网络连接失败，请检查网络")
            is JsonSyntaxException -> ParseException("数据解析失败，请联系管理员")
            else -> Exception("未知错误：${e.message}")
        }
    }
}