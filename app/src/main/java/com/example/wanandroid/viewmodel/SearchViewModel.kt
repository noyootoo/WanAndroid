package com.example.wanandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanandroid.model.Article
import com.example.wanandroid.model.UiState
import com.example.wanandroid.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {//由于是搜索页,不需要init自动加载
    private val repository = SearchRepository()
    private var currentPage = 0
    private var isLoading = false      // 是否正在初次加载
    private var isLoadingMore = false  // 是否正在上拉加载更多
    private var currentKeyword = "" // 🌟 必须记住当前搜的词
    private val currentArticles = mutableListOf<Article>()

    // 初始状态给一个普通的 Loading 或者自定义一个 Empty 状态，这里先用 Success(emptyList()) 代表一开始啥也没有
    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Success(emptyList()))
    val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()

    // 由 UI 层的“搜索”按钮主动调用
    fun search(keyword: String) {
        // 防呆设计：如果用户没输入东西就点搜索，直接拦截
        if (keyword.trim().isEmpty()||isLoading) {
            _uiState.value = UiState.Error("请输入要搜索的关键词哦")
            return
        }
        isLoading = true
        currentPage = 0 // 重新搜索，页码归零
        currentKeyword = keyword // 🌟 记住这个词！
        currentArticles.clear()
        // 开始搜索，广播 Loading 状态
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            repository.search(keyword, 0).fold(
                onSuccess = { articles ->
                    if (articles.isEmpty()) {
                        _uiState.value = UiState.Error("没有找到相关文章")
                    } else {
                        currentArticles.addAll(articles)
                        _uiState.value = UiState.Success(currentArticles.toList())
                        currentPage++
                    }
                },
                onFailure = { exception ->
                    _uiState.value = UiState.Error(exception.message ?: "搜索报错了")
                }
            )
            isLoading = false
        }
    }
    fun loadMore(keyword: String) {
        if (isLoading || isLoadingMore || currentKeyword.isEmpty()) return

        isLoadingMore = true

        viewModelScope.launch {
            repository.search(currentKeyword, currentPage).fold(
                onSuccess = { newArticles ->
                    currentArticles.addAll(newArticles)
                    // 拼接并提交全新 List
                    _uiState.value = UiState.Success(currentArticles.toList())
                    currentPage++
                },
                onFailure = { /* 静默失败，或者发个 Toast 事件 */ }
            )
            isLoadingMore = false
        }
    }
}
