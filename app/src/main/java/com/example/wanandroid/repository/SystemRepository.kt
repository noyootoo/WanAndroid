package com.example.wanandroid.repository

import com.example.wanandroid.model.SystemCategory
import com.example.wanandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SystemRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getSystemTree(): Result<List<SystemCategory>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSystemTree()
                if (response.errorCode == 0 && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.errorMsg ?: "服务器业务异常"))
                }
            } catch (e: Exception) {
                // 复用我们昨天写好的异常类
                Result.failure(Exception("网络或解析错误：${e.message}"))
            }
        }
    }
}