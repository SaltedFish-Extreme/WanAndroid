package com.example.wanAndroid.util.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * CookieJarImpl
 *
 * @author linzhiyong
 * @email wflinzhiyong@163.com
 * @blog https://www.jianshu.com/p/23b35d403148
 * @time 2018/7/20
 */
@Suppress("unused")
class CookieJarImpl(cookieStore: CookieStore?) : CookieJar {
    private val cookieStore: CookieStore

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.add(url, cookies)
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url]
    }

    init {
        requireNotNull(cookieStore) { "cookieStore can not be null." }
        this.cookieStore = cookieStore
    }
}