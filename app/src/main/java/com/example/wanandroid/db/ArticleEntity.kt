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
    // 修改：记录插入时的自增索引，因为 System.currentTimeMillis() 在批量插入时可能相同，导致排序混乱
    @androidx.room.ColumnInfo(name = "insert_index")
    val insertIndex: Int = 0
)

// 转换扩展方法，新增一个参数用于传递索引
fun Article.toEntity(index: Int = 0) = ArticleEntity(
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
    isRead = this.isRead,
    insertIndex = index
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
