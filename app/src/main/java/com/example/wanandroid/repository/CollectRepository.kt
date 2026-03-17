package com.example.wanandroid.repository

import com.example.wanandroid.model.ArticleListData
import com.example.wanandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CollectRepository {

    suspend fun collectArticle(id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.collectArticle(id)
                if (response.errorCode == 0) {
                    Result.success(true)
                } else {
                    Result.failure(ApiException(response.errorCode, response.errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun uncollectArticle(id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.uncollectArticle(id)
                if (response.errorCode == 0) {
                    Result.success(true)
                } else {
                    Result.failure(ApiException(response.errorCode, response.errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCollectList(page: Int): Result<ArticleListData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getCollectList(page)
                if (response.errorCode == 0 && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.errorCode, response.errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
