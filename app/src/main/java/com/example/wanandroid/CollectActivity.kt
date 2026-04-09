package com.example.wanandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.adapter.ArticleAdapter
import com.example.wanandroid.databinding.ActivityCollectBinding
import com.example.wanandroid.model.UiState
import com.example.wanandroid.viewmodel.CollectViewModel
import kotlinx.coroutines.launch

class CollectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectBinding
    private val viewModel: CollectViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter(
            onItemClick = { clickedArticle ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("article_data", clickedArticle)
                startActivity(intent)
            },
            onCollectClick = { clickedArticle ->
                // 在收藏列表里点击收藏按钮，就是取消收藏
                viewModel.uncollect(clickedArticle)
            }
        )
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCollects(isRefresh = true)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state !is UiState.Loading) {
                        binding.swipeRefresh.isRefreshing = false
                        binding.progressBar.visibility = View.GONE
                    }

                    when (state) {
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                        is UiState.Success -> {
                            adapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            Toast.makeText(this@CollectActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
