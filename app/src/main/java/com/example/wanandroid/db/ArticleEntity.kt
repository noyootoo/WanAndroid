package com.example.wanandroid.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wanandroid.model.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val author: String,
    val shareUser: String,
    val niceDate: String,
    val link: String,
    val chapterId: Int,
    val chapterName: String,
    val envelopePic: String,
    val collect: Boolean,
    val isRead: Boolean,
    // 记录插入时间，以便后续排序（因为网络数据是有序的）
    val insertTime: Long = System.currentTimeMillis()
)

// 转换扩展方法
fun Article.toEntity() = ArticleEntity(
    id = this.id,
    title = this.title,
    author = this.author ?: "",
    shareUser = this.shareUser ?: "",
    niceDate = this.niceDate,
    link = this.link,
    chapterId = this.chapterId ?: 0,
    chapterName = this.chapterName ?: "",
    envelopePic = this.envelopePic,
    collect = this.collect,
    isRead = this.isRead
)

fun ArticleEntity.toModel() = Article(
    id = this.id,
    title = this.title,
    author = this.author,
    shareUser = this.shareUser,
    niceDate = this.niceDate,
    link = this.link,
    chapterId = this.chapterId,
    chapterName = this.chapterName,
    envelopePic = this.envelopePic,
    collect = this.collect,
    isRead = this.isRead
)
