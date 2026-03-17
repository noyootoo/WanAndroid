package com.example.wanandroid.network

import com.example.wanandroid.model.ArticleListData
import com.example.wanandroid.model.BaseResponse
import com.example.wanandroid.model.SystemCategory
import com.example.wanandroid.model.UserInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    // 获取首页文章列表（分页）
    // @GET 表示发送 GET 请求，{page} 是占位符
    @GET("article/list/{page}/json")
    suspend fun getArticleList(
        @Path("page") page: Int  // @Path 会把这里的 page 填入上面的 {page} 中
    ): BaseResponse<ArticleListData>

    @GET("tree/json")
    suspend fun getSystemTree(): BaseResponse<List<SystemCategory>>

    @FormUrlEncoded
    @POST("article/query/{page}/json")
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Field("k") keyword: String
    ): BaseResponse<ArticleListData> // 返回的数据结构和首页完全一样！

    // 登录
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<UserInfo>

    // 注册
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): BaseResponse<UserInfo>

    // 收藏文章
    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(
        @Path("id") id: Int
    ): BaseResponse<Any?>

    // 取消收藏文章（文章列表）
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollectArticle(
        @Path("id") id: Int
    ): BaseResponse<Any?>

    // 获取收藏列表
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(
        @Path("page") page: Int
    ): BaseResponse<ArticleListData>
}