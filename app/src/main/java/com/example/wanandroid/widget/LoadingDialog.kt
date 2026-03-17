package com.example.wanandroid.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.wanandroid.R

class LoadingDialog(context: Context) : Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_loading_dialog)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}