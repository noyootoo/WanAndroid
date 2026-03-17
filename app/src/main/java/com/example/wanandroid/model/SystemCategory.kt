package com.example.wanandroid.model

data class SystemCategory(
    val id: Int,
    val name: String,
    val children: List<SystemChild>
)

data class SystemChild(
    val id: Int,
    val name: String
)