package com.example.wanAndroid.logic.model

import com.example.wanAndroid.logic.model.base.ApiPagerResponse

/** 分享列表数据类 */
data class ShareResponse(
    val shareArticles: ApiPagerResponse<ArrayList<ArticleResponse>>
)