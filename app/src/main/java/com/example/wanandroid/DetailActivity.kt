package com.example.wanandroid // 你的包名

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.wanandroid.databinding.ActivityDetailBinding
import com.example.wanandroid.model.Article

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityDetailBinding.inflate(layoutInflater)
        // 🔴 必须加上这句！把 binding 的根视图（整个页面）设置给 Activity 显示！
        setContentView(binding.root)

        val webView = binding.webView
        val progressBar =  binding.progressBar

        // 1. 拆快递：取出传递过来的 Article 对象
        val article = intent.getParcelableExtra<Article>("article_data")

        // 2. 配置并加载 WebView
        webView.apply {
            settings.javaScriptEnabled = true // 允许执行 JS

            webViewClient = object : WebViewClient() {
                // 网页加载完成时，隐藏中间的进度条
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                }
            }

            // 3. 开始加载网址
            if (article != null && article.link.isNotEmpty()) {
                loadUrl(article.link)
            }
        }
    }
}