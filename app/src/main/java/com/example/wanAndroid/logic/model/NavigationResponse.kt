package com.example.wanAndroid.logic.model

/** 导航数据类 */
data class NavigationResponse(
    val articles: MutableList<ArticleResponse>,
    val cid: Int,
    val name: String
)