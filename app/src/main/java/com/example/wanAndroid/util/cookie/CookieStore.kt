package com.example.wanAndroid.util.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * Cookie缓存接口
 *
 * @author linzhiyong
 * @email wflinzhiyong@163.com
 * @blog https://www.jianshu.com/p/23b35d403148
 */
interface CookieStore {
    /** 添加cookie */
    fun add(httpUrl: HttpUrl, cookie: Cookie)

    /** 添加指定httpUrl cookie集合 */
    fun add(httpUrl: HttpUrl, cookies: List<Cookie>)

    /** 根据HttpUrl从缓存中读取cookie集合 */
    operator fun get(httpUrl: HttpUrl): List<Cookie>

    /** 获取所有缓存cookie */
    val cookies: List<Cookie>

    /** 移除指定httpUrl cookie集合 */
    fun remove(httpUrl: HttpUrl, cookie: Cookie): Boolean

    /** 移除全部cookie */
    fun removeAll(): Boolean
}