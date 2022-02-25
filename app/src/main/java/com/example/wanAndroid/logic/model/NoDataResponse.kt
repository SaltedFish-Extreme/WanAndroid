package com.example.wanAndroid.logic.model

/** 返回数据为空的数据类 */
data class NoDataResponse(val errorCode: Int, val errorMsg: String, val data: Nothing? = null)