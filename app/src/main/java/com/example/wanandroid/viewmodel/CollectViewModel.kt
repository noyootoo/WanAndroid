package com.example.wanandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanandroid.model.Article
import com.example.wanandroid.model.UiState
import com.example.wanandroid.repository.CollectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectViewModel : ViewModel() {
    private var currentPage = 0
    private var isLoading = false
    private var isLoadingMore = false

    private val repository = CollectRepository()

    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()

    init {
        loadCollects()
    }

    fun loadCollects(isRefresh: Boolean = false) {
        if (isLoading) return
        isLoading = true
        if (isRefresh) currentPage = 0

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            repository.getCollectList(currentPage).fold(
                onSuccess = { data ->
                    // 收藏列表里的文章，默认收藏状态都是 true
                    val articles = data.datas.map { it.copy(collect = true) }
                    _uiState.value = UiState.Success(articles)
                    currentPage++
                    isLoading = false
                },
                onFailure = {
                    _uiState.value = UiState.Error(it.message ?: "加载失败")
                    isLoading = false
                }
            )
        }
    }

    fun loadMore() {
        if (isLoading || isLoadingMore) return
        isLoadingMore = true

        viewModelScope.launch {
            repository.getCollectList(currentPage).fold(
                onSuccess = { data ->
                    val currentData = (_uiState.value as? UiState.Success)?.data ?: emptyList()
                    val newArticles = data.datas.map { it.copy(collect = true) }
                    _uiState.value = UiState.Success(currentData + newArticles)
                    currentPage++
                    isLoadingMore = false
                },
                onFailure = {
                    _uiState.value = UiState.Error(it.message ?: "加载更多失败")
                    isLoadingMore = false
                }
            )
        }
    }

    fun uncollect(article: Article) {
        val currentList = (_uiState.value as? UiState.Success)?.data?.toMutableList() ?: return
        
        // 乐观更新：直接从列表中移除
        val newList = currentList.filter { it.id != article.originId && it.id != article.id }
        _uiState.value = UiState.Success(newList)

        viewModelScope.launch {
            // 注意：取消收藏文章列表里的文章，需要传 originId 或 id，WanAndroid 收藏列表里的 id 是收藏id，originId 是原文章 id。
            // 这里为了简单，我们调用 uncollectArticle 并传入 originId，如果 originId 没有，就传 id
            val targetId = if (article.originId != 0) article.originId else article.id
            repository.uncollectArticle(targetId).onFailure {
                // 如果失败了，加回来
                _uiState.value = UiState.Success(currentList)
                _uiState.value = UiState.Error("取消收藏失败")
            }
        }
    }
}
