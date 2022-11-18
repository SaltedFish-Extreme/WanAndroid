package com.example.wanAndroid.util.cookie

import okhttp3.Cookie
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 仿照android-async-http的SerializableCookie实现，用处是cookie对象与对象流的互转，保存和读取cookie
 *
 * @author linzhiyong
 * @email wflinzhiyong@163.com
 * @blog https://www.jianshu.com/p/23b35d403148
 * @time 2018/7/20
 */
class SerializableCookie(@field:Transient private val cookie: Cookie) : Serializable {
    @Transient
    private var clientCookie: Cookie? = null
    fun getCookie(): Cookie? {
        var bestCookie: Cookie? = cookie
        if (clientCookie != null) {
            bestCookie = clientCookie
        }
        return bestCookie
    }

    /** 将cookie写到对象流中 */
    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name)
        out.writeObject(cookie.value)
        out.writeLong(cookie.expiresAt)
        out.writeObject(cookie.domain)
        out.writeObject(cookie.path)
        out.writeBoolean(cookie.secure)
        out.writeBoolean(cookie.httpOnly)
        out.writeBoolean(cookie.hostOnly)
        out.writeBoolean(cookie.persistent)
    }

    /** 从对象流中构建cookie对象 */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        val name = `in`.readObject() as String
        val value = `in`.readObject() as String
        val expiresAt = `in`.readLong()
        val domain = `in`.readObject() as String
        val path = `in`.readObject() as String
        val secure = `in`.readBoolean()
        val httpOnly = `in`.readBoolean()
        val hostOnly = `in`.readBoolean()
        val persistent = `in`.readBoolean()
        var builder: Cookie.Builder = Cookie.Builder()
            .name(name)
            .value(value)
            .expiresAt(expiresAt)
            .path(path)
        builder = if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
        builder = if (secure) builder.secure() else builder
        builder = if (httpOnly) builder.httpOnly() else builder
        clientCookie = builder.build()
    }

    companion object {
        private const val serialVersionUID = 6374381828722046732L
    }
}