package com.example.wanAndroid.util

import android.text.TextUtils
import android.util.Log
import com.example.wanAndroid.BuildConfig
import com.example.wanAndroid.util.KLogHelper.wrapMessage

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: 自定义Log打印工具类
 */
object KLog {

    //默认Tag
    private var tag = "KLog"

    fun setTag(tag: String) {
        if (!TextUtils.isEmpty(tag)) {
            KLog.tag = tag
        }
    }

    //判断是否是Debug模式，是就打印日志信息，否则不打印日志信息
    private var isDebugMode = BuildConfig.DEBUG

    fun setDebugMode(debugMode: Boolean) {
        isDebugMode = debugMode
    }

    //判断是否是Link模式，是就打印堆栈信息，否则不打印堆栈信息
    private var isLinkMode = true

    fun setLinkMode(linkMode: Boolean) {
        isLinkMode = linkMode
    }

    //堆栈索引数，最小为1
    private const val CALL_STACK_INDEX = 3

    fun v(tag: String, msg: String): Int {
        return if (isDebugMode) Log.v(mapTag(tag), mapMsg(msg)) else -1
    }

    fun v(msg: String): Int {
        return if (isDebugMode) Log.v(tag, mapMsg(msg)) else -1
    }

    fun d(tag: String, msg: String): Int {
        return if (isDebugMode) Log.d(mapTag(tag), mapMsg(msg)) else -1
    }

    fun d(msg: String): Int {
        return if (isDebugMode) Log.d(tag, mapMsg(msg)) else -1
    }

    fun i(tag: String, msg: String): Int {
        return if (isDebugMode) Log.i(mapTag(tag), mapMsg(msg)) else -1
    }

    fun i(msg: String): Int {
        return if (isDebugMode) Log.i(tag, mapMsg(msg)) else -1
    }

    fun w(tag: String, msg: String): Int {
        return if (isDebugMode) Log.w(mapTag(tag), mapMsg(msg)) else -1
    }

    fun w(msg: String): Int {
        return if (isDebugMode) Log.w(tag, mapMsg(msg)) else -1
    }

    fun e(tag: String, msg: String): Int {
        return if (isDebugMode) Log.e(mapTag(tag), mapMsg(msg)) else -1
    }

    fun e(msg: String): Int {
        return if (isDebugMode) Log.e(tag, mapMsg(msg)) else -1
    }

    private fun mapMsg(msg: String): String {
        return if (isLinkMode) wrapMessage(CALL_STACK_INDEX, msg) else msg
    }

    private fun mapTag(tag: String): String {
        return if (TextUtils.isEmpty(tag)) KLog.tag else tag
    }
}