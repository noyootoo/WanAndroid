package com.example.wanandroid

import android.app.Application
import android.content.Context

class WanApplication : Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
