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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// ✅ 新增：导入生成的 Binding 类
import com.example.wanandroid.adapter.ArticleAdapter
import com.example.wanandroid.base.BaseFragment
import com.example.wanandroid.databinding.FragmentHomeBinding
import com.example.wanandroid.model.UiState
import com.example.wanandroid.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    // 保持不变：自动帮你创建并管理 ViewModel 的生死
    private val viewModel: HomeViewModel by viewModels()

    // ✅ 新增：把 adapter 提出来作为全局变量，因为下面的协程里也要用到它
    private lateinit var adapter: ArticleAdapter

    override fun initView() {
        setupRecyclerView()   // 1. 初始化列表和上拉加载
        setupSwipeRefresh()   // 2. 初始化下拉刷新
    }

    override fun initObserver() {
        observeUiState()      // 3. 监听数据变化
    }
    private    fun setupRecyclerView() {
        adapter = ArticleAdapter(
            onItemClick = { clickedArticle ->
                if (!clickedArticle.isRead) {
                    // 更新本地数据库，UI 会自动响应
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
        // --- 核心：添加滚动监听器实现上拉加载 ---
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // dy > 0 表示正在向上滑动（看下面的内容）
                if (dy > 0) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    // 如果列表总数 减去 当前看到的最后一个 item 的位置 <= 3，说明快到底了
                    if (totalItemCount - lastVisibleItem <= 3) {
                        viewModel.loadMore() // 呼叫 ViewModel 加载下一页
                    }
                }
            }
        })
    }

    private   fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            // 设置转圈圈的颜色（可选）
            setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)

            // 下拉刷新监听
            setOnRefreshListener {
                // 触发 ViewModel 的下拉刷新逻辑
                viewModel.loadArticles(isRefresh = true)
            }
        }
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                Log.d("MVVM", "正在拼命加载中...")
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
                                Log.d("MVVM", "成功拿到了 ${state.data.size} 条真实数据！")
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
                                Log.e("MVVM", "请求失败：${state.message}")
                            }
                        }
                    }
                }
                
                launch {
                    viewModel.refreshCompleteEvent.collect {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }
    // 移除不必要的 onDestroyView
}