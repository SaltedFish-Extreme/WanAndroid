package com.example.wanAndroid.logic.net

import com.drake.net.convert.JSONConvert
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

/**
 * Created by 咸鱼至尊 on 2022/1/8
 *
 * desc: Gson解析转换器
 */
class GsonConvert : JSONConvert(code = "errorCode", message = "errorMsg", success = "0") {
    private val gson: Gson = GsonBuilder().serializeNulls().create()

    override fun <S> String.parseBody(succeed: Type): S? {
        return gson.fromJson(this, succeed)
    }
}