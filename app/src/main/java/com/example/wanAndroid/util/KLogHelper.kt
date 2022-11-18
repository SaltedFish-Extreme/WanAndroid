package com.example.wanAndroid.util

import java.util.regex.Pattern

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: 自定义Log帮助类
 */
object KLogHelper {
    private const val CALL_STACK_INDEX = 1
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    @JvmStatic
    fun wrapMessage(stackIndex: Int, message: String): String {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace().
        var mStackIndex = stackIndex
        var mMessage = message
        if (mStackIndex < 0) {
            mStackIndex = CALL_STACK_INDEX
        }
        val stackTrace = Throwable().stackTrace
        check(stackTrace.size > mStackIndex) { "Synthetic stacktrace didn't have enough elements: are you using proguard?" }
        val clazz = extractClassName(stackTrace[mStackIndex])
        val lineNumber = stackTrace[mStackIndex].lineNumber
        mMessage = "($clazz.java:$lineNumber) - $mMessage"
        return mMessage
    }

    /**
     * Extract the class name without any anonymous class suffixes (e.g.,
     * `Foo$1` becomes `Foo`).
     */
    private fun extractClassName(element: StackTraceElement): String {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag.substring(tag.lastIndexOf('.') + 1)
    }
}