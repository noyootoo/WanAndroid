package com.example.wanandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.adapter.ArticleAdapter
import com.example.wanandroid.base.BaseFragment
import com.example.wanandroid.databinding.FragmentHomeBinding
import com.example.wanandroid.model.UiState
import com.example.wanandroid.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun initView() {
        setupRecyclerView()
        setupSwipeRefresh()
    }

    override fun initObserver() {
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter(
            onItemClick = { clickedArticle ->
                if (!clickedArticle.isRead) {
                    viewModel.markArticleRead(clickedArticle.id)
                }
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra("article_data", clickedArticle)
                startActivity(intent)
            },
            onCollectClick = { clickedArticle ->
                viewModel.toggleCollect(clickedArticle)
            }
        )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (totalItemCount - lastVisibleItem <= 3) {
                        viewModel.loadMore()
                    }
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
            setOnRefreshListener {
                viewModel.loadArticles(isRefresh = true)
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        if (!binding.swipeRefresh.isRefreshing && adapter.currentList.isEmpty()) {
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
                                viewModel.loadArticles(isRefresh = true)
                            }
                        } else {
                            showToast(state.message)
                        }
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.refreshCompleteEvent.collect {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}