package com.example.wanandroid.repository

import com.example.wanandroid.db.AppDatabase
import com.example.wanandroid.db.toEntity
import com.example.wanandroid.db.toModel
import com.example.wanandroid.model.Article
import com.example.wanandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import com.google.gson.JsonSyntaxException

class ArticleRepository {

    // 1. 拿到厨师（ApiService）
    private val apiService = RetrofitClient.apiService
    // 拿到本地数据库
    private val articleDao = AppDatabase.getDatabase().articleDao()

    // 观察本地数据库的数据流 (Single Source of Truth)
    fun getArticlesFlow(): Flow<List<Article>> {
        return articleDao.getAllArticlesFlow().map { entities ->
            entities.map { it.toModel() }
        }
    }

    // 2. 告诉厨师做菜，并且必须在后台（IO线程）做！
    // 这里的 fetchArticleList 现在只负责从网络拉取数据，并存入数据库
    suspend fun fetchArticleList(page: Int, isRefresh: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 去网络拿数据
                val response = apiService.getArticleList(page)

                // 🌟 新增：业务级错误判断
                if (response.errorCode == 0 && response.data != null) {
                    val articles = response.data.datas
                    
                    if (isRefresh) {
                        // 如果是刷新，先清空旧数据
                        articleDao.clearAll()
                    }
                    
                    // 获取当前数据库中最大的 insert_index
                    // 如果是刷新（刚才被 clearAll 了）或者原本就为空，getMaxInsertIndex 会返回 null，此时我们从 0 开始
                    val currentMaxIndex = articleDao.getMaxInsertIndex() ?: -1
                    
                    // 新数据的索引严格在当前最大索引基础上累加
                    val entities = articles.mapIndexed { index, article -> 
                        article.toEntity(currentMaxIndex + 1 + index) 
                    }
                    
                    // 将新数据存入数据库
                    articleDao.insertAll(entities)
                    
                    Result.success(Unit)
                } else {
                    // 把服务器给的错误信息包在 ApiException 里抛出去
                    Result.failure(ApiException(response.errorCode, response.errorMsg ?: "服务器业务异常"))
                }

            } catch (e: Exception) {
                // 🌟 新增：把系统生硬的异常，翻译成我们自定义的异常
                val finalException = when (e) {
                    is SocketTimeoutException -> NetworkException("网络超时，请稍后重试")
                    is IOException -> NetworkException("网络连接失败，请检查网络")
                    is JsonSyntaxException -> ParseException("数据解析失败，请联系管理员")
                    else -> Exception("未知错误：${e.message}")
                }
                // 把翻译好的异常返回给 ViewModel
                Result.failure(finalException)
            }
        }
    }
    
    // 更新本地文章的已读状态
    suspend fun updateReadStatus(id: Int, isRead: Boolean) {
        withContext(Dispatchers.IO) {
            articleDao.updateReadStatus(id, isRead)
        }
    }

    // 更新本地文章的收藏状态
    suspend fun updateCollectStatus(id: Int, collect: Boolean) {
        withContext(Dispatchers.IO) {
            articleDao.updateCollectStatus(id, collect)
        }
    }
}

// 🌟 新增：在文件最底部，定义我们自己的三种异常类型
class ApiException(val code: Int, message: String) : Exception(message) {
    constructor(message: String) : this(-1, message)
}
class NetworkException(message: String) : Exception(message)
class ParseException(message: String) : Exception(message)