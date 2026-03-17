package com.example.wanandroid

import android.content.Intent
import android.os.Bundle
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
import com.example.wanandroid.databinding.FragmentSearchBinding
import com.example.wanandroid.model.UiState
import com.example.wanandroid.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun initView() {
        setupRecyclerView()

        binding.btnSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            viewModel.search(keyword)
        }
    }

    override fun initObserver() {
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter(
            onItemClick = { clickedArticle ->
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra("article_data", clickedArticle)
                startActivity(intent)
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
                        viewModel.loadMore(binding.etSearch.text.toString())
                    }
                }
            }
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        showLoading()
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
                        if (state.message == "没有找到相关文章") {
                            adapter.submitList(emptyList())
                            showEmptyState("没有找到相关文章")
                        } else {
                            if (adapter.currentList.isEmpty()) {
                                showErrorState("加载失败，点击重试") {
                                    val keyword = binding.etSearch.text.toString()
                                    if (keyword.isNotEmpty()) {
                                        viewModel.search(keyword)
                                    }
                                }
                            } else {
                                showToast(state.message)
                            }
                        }
                    }
                }
            }
        }
    }
}