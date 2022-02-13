package com.example.wanAndroid.webclient

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: 缓存拦截器
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldReq = chain.request()
        return chain.proceed(oldReq)
    }
}