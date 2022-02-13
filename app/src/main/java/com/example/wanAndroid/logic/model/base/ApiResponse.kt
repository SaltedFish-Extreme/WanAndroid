package com.example.wanAndroid.logic.model.base

/**
 * 作者：何高建
 *
 * 时间：2019/12/23
 *
 * 描述：服务器返回数据的基类
 *
 * 如果你的项目中有基类，那美滋滋，可以继承BaseResponse，请求时框架可以帮你自动脱壳，自动判断是否请求成功，怎么做：
 *
 * 1.继承 BaseResponse
 *
 * 2.重写isSuccess方法，编写你的业务需求，根据自己的条件判断数据是否请求成功
 *
 * 3.重写 getResponseCode、getResponseData、getResponseMsg方法，传入你的 code data msg
 */
data class ApiResponse<T>(val errorCode: Int, val errorMsg: String, val data: T) : BaseResponse<T>() {

    // 这里是示例，wanAndroid 网站返回的 错误码为 0 就代表请求成功，请你根据自己的业务需求来改变
    override fun isSuccess() = errorCode == 0

    override fun getResponseCode() = errorCode

    override fun getResponseData() = data

    override fun getResponseMsg() = errorMsg

}