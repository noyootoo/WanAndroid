package com.example.wanandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanandroid.model.SystemCategory
import com.example.wanandroid.model.UiState
import com.example.wanandroid.repository.SystemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SystemViewModel : ViewModel() {
    private val repository = SystemRepository()

    private val _uiState = MutableStateFlow<UiState<List<SystemCategory>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<SystemCategory>>> = _uiState.asStateFlow()

    init {
        // ViewModel 一出生就去加载数据
        loadSystemTree()
    }

    fun loadSystemTree() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.getSystemTree().fold(
                onSuccess = { categories ->
                    _uiState.value = UiState.Success(categories)
                },
                onFailure = { exception ->
                    _uiState.value = UiState.Error(exception.message ?: "加载失败")
                }
            )
        }
    }
    fun loadMore() {
        // 模拟加载更多
        viewModelScope.launch {
            _uiState.value = UiState.Success(emptyList())
        }
    }
}