package com.example.wanandroid.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wanandroid.WanApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 创建 DataStore 的扩展属性
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object DataStoreManager {
    private val dataStore = WanApplication.context.dataStore

    // 定义 Key
    private val COOKIE_KEY = stringPreferencesKey("cookie")
    private val USERNAME_KEY = stringPreferencesKey("username")

    // 保存 Cookie
    suspend fun saveCookie(cookie: String) {
        dataStore.edit { preferences ->
            preferences[COOKIE_KEY] = cookie
        }
    }

    // 获取 Cookie 的 Flow
    val cookieFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[COOKIE_KEY] ?: ""
    }

    // 保存用户名
    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    // 获取用户名的 Flow
    val usernameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[USERNAME_KEY] ?: ""
    }
    
    // 清除用户数据（退出登录时使用）
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(COOKIE_KEY)
            preferences.remove(USERNAME_KEY)
        }
    }
}
