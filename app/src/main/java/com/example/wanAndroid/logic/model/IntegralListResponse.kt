package com.example.wanAndroid.logic.model

/** 积分获取列表数据类 */
data class IntegralListResponse(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Int,
    val reason: String,
    val type: Int,
    val userId: Int,
    val userName: String
)