package com.example.wanandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanandroid.adapter.SystemAdapter
import com.example.wanandroid.base.BaseFragment
import com.example.wanandroid.databinding.FragmentSystemBinding
import com.example.wanandroid.model.UiState
import com.example.wanandroid.viewmodel.SystemViewModel
import kotlinx.coroutines.launch

class SystemFragment : BaseFragment<FragmentSystemBinding>(FragmentSystemBinding::inflate) {

    private val viewModel: SystemViewModel by viewModels()
    private lateinit var adapter: SystemAdapter

    override fun initView() {
        adapter = SystemAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadSystemTree()
        }
    }

    override fun initObserver() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state !is UiState.Loading) {
                    binding.swipeRefresh.isRefreshing = false
                }

                when (state) {
                    is UiState.Loading -> {
                        if (!binding.swipeRefresh.isRefreshing) {
                            showLoading()
                        }
                    }
                    is UiState.Success -> {
                        hideLoading()
                        if (state.data.isEmpty()) {
                            showEmptyState()
                        } else {
                            hideStateLayout()
                        }
                        adapter.submitList(state.data)
                    }
                    is UiState.Error -> {
                        hideLoading()
                        if (adapter.currentList.isEmpty()) {
                            showErrorState("加载失败，点击重试") {
                                viewModel.loadSystemTree()
                            }
                        } else {
                            showToast(state.message)
                        }
                    }
                }
            }
        }
    }

    // 移除不必要的 onDestroyView
}


