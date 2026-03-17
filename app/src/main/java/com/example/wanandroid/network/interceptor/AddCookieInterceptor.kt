package com.example.wanandroid.network.interceptor

import com.example.wanandroid.utils.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 在发起网络请求前，自动将本地保存的 Cookie 注入到请求头中
 */
class AddCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 从 DataStore 中读取本地保存的 Cookie
        // 拦截器运行在 OkHttp 的后台线程池中，使用 runBlocking 是安全的
        val cookieStr = runBlocking {
            DataStoreManager.cookieFlow.first()
        }
        
        if (cookieStr.isNotEmpty()) {
            // 将保存的 Cookie 添加到请求头
            // 注意：OkHttp 提供 header 和 addHeader 方法，addHeader 用于附加，不会覆盖现有头部
            val newRequest = request.newBuilder()
                .addHeader("Cookie", cookieStr)
                .build()
            
            // 发起带有 Cookie 的请求
            return chain.proceed(newRequest)
        }
        
        // 如果没有保存的 Cookie，直接发起原始请求
        return chain.proceed(request)
    }
}
