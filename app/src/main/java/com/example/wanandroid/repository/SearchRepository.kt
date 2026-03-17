package com.example.wanandroid.repository

import com.example.wanandroid.model.Article
import com.example.wanandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun search(keyword: String, page: Int = 0): Result<List<Article>> {
        return withContext(Dispatchers.IO) {
            try {
                // 调用搜索接口
                val response = apiService.searchArticles(page, keyword)
                if (response.errorCode == 0 && response.data != null) {
                    // 取出文章列表
                    Result.success(response.data.datas)
                } else {
                    Result.failure(ApiException(response.errorMsg ?: "搜索失败"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("网络错误：${e.message}"))
            }
        }
    }

}