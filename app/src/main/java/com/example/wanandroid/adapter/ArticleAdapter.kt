package com.example.wanandroid.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wanandroid.databinding.ItemArticleBinding
import com.example.wanandroid.model.Article

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onCollectClick: (Article) -> Unit = {}
) : ListAdapter<Article, ArticleAdapter.ViewHolder>(ArticleDiffCallback()) {

    class ViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article, onItemClick: (Article) -> Unit, onCollectClick: (Article) -> Unit) {
            binding.apply {
                tvTitle.text = HtmlCompat.fromHtml(article.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
                if (article.envelopePic.isNullOrEmpty()) {
                    ivCover.visibility = View.GONE
                } else {
                    ivCover.visibility = View.VISIBLE
                    ivCover.load(article.envelopePic) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_dialog_alert)
                    }
                }

                tvAuthor.text = if (article.author.isNullOrEmpty()) article.shareUser else article.author
                tvChapter.text = article.chapterName
                tvTime.text = article.niceDate

                if (article.isRead) {
                    tvTitle.setTextColor(Color.GRAY)
                } else {
                    tvTitle.setTextColor(Color.parseColor("#1D2129"))
                }
                
                if (article.collect) {
                    ivCollect.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    ivCollect.setImageResource(android.R.drawable.btn_star_big_off)
                }

                root.setOnClickListener {
                    onItemClick(article)
                }
                
                ivCollect.setOnClickListener {
                    onCollectClick(article)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onCollectClick)
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}