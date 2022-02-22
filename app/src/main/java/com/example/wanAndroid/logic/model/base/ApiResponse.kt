package com.example.wanAndroid.logic.model.base

/** 服务器返回数据的基类 */
data class ApiResponse<T>(val errorCode: Int, val errorMsg: String, val data: T)