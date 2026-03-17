package com.example.wanandroid.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class BaseResponse<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String
)

data class ArticleListData(
    val datas: List<Article>,
    val curPage: Int,
    val pageCount: Int
)

@Parcelize
data class Article(
    val id: Int,
    val title: String,
    val author: String? = "",
    val shareUser: String? = "",
    val niceDate: String,
    val link: String,
    val chapterId: Int? = 0,
    val chapterName: String? = "",
    val originId: Int = 0,
    val envelopePic: String = "",
    var collect: Boolean = false,
    var isRead: Boolean = false
) : Parcelable