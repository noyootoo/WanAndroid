package com.example.wanandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanandroid.model.Article
import com.example.wanandroid.model.UiState
import com.example.wanandroid.repository.ApiException
import com.example.wanandroid.repository.ArticleRepository
import com.example.wanandroid.repository.CollectRepository
import com.example.wanandroid.repository.NetworkException
import com.example.wanandroid.repository.ParseException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private var currentPage = 0
    private var isLoading = false
    private var isLoadingMore = false

    private val repository = ArticleRepository()
    private val collectRepository = CollectRepository()

    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()

    private val _refreshCompleteEvent = MutableSharedFlow<Unit>()
    val refreshCompleteEvent = _refreshCompleteEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.getArticlesFlow().collectLatest { articles ->
                if (articles.isNotEmpty()) {
                    _uiState.value = UiState.Success(articles)
                } else {
                    if (!isLoading && !isLoadingMore) {
                        _uiState.value = UiState.Loading
                    }
                }
            }
        }
        
        loadArticles()
    }

    fun loadArticles(isRefresh: Boolean = false) {
        if (isLoading) return
        isLoading = true

        if (isRefresh) {
            currentPage = 0
        }

        viewModelScope.launch {
            repository.fetchArticleList(currentPage, isRefresh).fold(
                onSuccess = {
                    currentPage++
                    isLoading = false
                    if (isRefresh) {
                        _refreshCompleteEvent.emit(Unit)
                    }
                },
                onFailure = { exception ->
                    val errorMsg = when (exception) {
                        is NetworkException -> exception.message ?: "网络开小差了"
                        is ApiException -> exception.message ?: "服务器傲娇了"
                        is ParseException -> exception.message ?: "App解析出错了"
                        else -> "未知错误：${exception.message}"
                    }
                    _uiState.value = UiState.Error(errorMsg)
                    isLoading = false
                    if (isRefresh) {
                        _refreshCompleteEvent.emit(Unit)
                    }
                }
            )
        }
    }
    
    fun loadMore() {
        if (isLoading || isLoadingMore) return
        isLoadingMore = true

        viewModelScope.launch {
            repository.fetchArticleList(currentPage, false).fold(
                onSuccess = {
                    currentPage++
                    isLoadingMore = false
                },
                onFailure = { exception ->
                    val errorMsg = when (exception) {
                        is NetworkException -> exception.message ?: "加载下一页时网络断开了"
                        is ApiException -> exception.message ?: "加载下一页时服务器报错了"
                        is ParseException -> exception.message ?: "下一页数据解析失败"
                        else -> "未知错误：${exception.message}"
                    }
                    _uiState.value = UiState.Error(errorMsg)
                    isLoadingMore = false
                }
            )
        }
    }

    fun toggleCollect(article: Article) {
        val isCurrentlyCollected = article.collect

        viewModelScope.launch {
            repository.updateCollectStatus(article.id, !isCurrentlyCollected)

            val result = if (isCurrentlyCollected) {
                collectRepository.uncollectArticle(article.id)
            } else {
                collectRepository.collectArticle(article.id)
            }

            result.onFailure { exception ->
                repository.updateCollectStatus(article.id, isCurrentlyCollected)
                
                val errorMsg = when (exception) {
                    is NetworkException -> exception.message ?: "网络错误"
                    is ApiException -> "请先登录"
                    else -> "收藏操作失败"
                }
                _uiState.value = UiState.Error(errorMsg)
            }
        }
    }

    fun markArticleRead(id: Int) {
        viewModelScope.launch {
            repository.updateReadStatus(id, true)
        }
    }
}