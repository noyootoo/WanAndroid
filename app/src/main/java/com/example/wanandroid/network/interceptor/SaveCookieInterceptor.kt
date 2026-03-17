package com.example.wanandroid.network.interceptor

import com.example.wanandroid.utils.DataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 拦截并保存服务端返回的 Cookie
 */
class SaveCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // 发起网络请求，获取响应
        val response = chain.proceed(request)
        
        val requestUrl = request.url.toString()
        
        // 判断是否是登录或注册接口
        if (requestUrl.contains("user/login") || requestUrl.contains("user/register")) {
            val cookies = response.headers("Set-Cookie")
            if (cookies.isNotEmpty()) {
                // WanAndroid 登录会返回多个 Set-Cookie 响应头（如 loginUserName, token_pass, JSESSIONID）
                val cookieList = mutableListOf<String>()
                for (header in cookies) {
                    // 每个 Set-Cookie 头格式如： "loginUserName=xxx; Expires=xxx; Path=/"
                    // 客户端再次请求时只需要带上分号前面的键值对即可
                    val cookieNameValue = header.substringBefore(";")
                    cookieList.add(cookieNameValue)
                }
                
                val cookieStr = cookieList.joinToString(";")
                
                // 使用 DataStore 将提取出的 Cookie 持久化保存到本地
                // 拦截器运行在 OkHttp 的后台线程池中，使用 runBlocking 是安全的
                runBlocking {
                    DataStoreManager.saveCookie(cookieStr)
                }
            }
        }
        
        return response
    }
}
