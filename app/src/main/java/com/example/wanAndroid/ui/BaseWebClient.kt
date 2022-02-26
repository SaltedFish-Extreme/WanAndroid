package com.example.wanAndroid.ui

import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.just.agentweb.WebViewClient

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: 通用web客户端
 */
open class BaseWebClient : WebViewClient() {
    // 拦截的网址
    private val blackHostList = arrayListOf(
        "taobao.com",
        "jd.com",
        "alipay.com",
        "www.ilkwork.com",
        "cnzz.com",
        "yun.tuisnake.com",
        "yun.lvehaisen.com",
        "yun.tuitiger.com",
        "ad.lflucky.com",
        "downloads.jianshu.io",
        "juejin.zlink.toutiao.com",
        "app-wvhzpj.openinstall.io"
    )

    private fun isBlackHost(host: String): Boolean {
        for (blackHost in blackHostList) {
            if (blackHost == host) {
                return true
            }
        }
        return false
    }

    private fun shouldInterceptRequest(uri: Uri): Boolean {
        val host = uri.host ?: ""
        if (host.startsWith("intent") || host.startsWith("alipay") || host.contains("alipay")) run {
            return true
        }
        return isBlackHost(host)
    }

    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
        if (shouldInterceptRequest(request.url)) {
            return WebResourceResponse(null, null, null)
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return true
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        handler?.cancel()
    }
}