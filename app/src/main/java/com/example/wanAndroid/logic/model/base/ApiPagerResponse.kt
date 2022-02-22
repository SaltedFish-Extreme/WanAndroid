package com.example.wanAndroid.logic.model.base

/** 分页数据的基类 */
data class ApiPagerResponse<T>(
    val datas: T,
    val curPage: Int,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)