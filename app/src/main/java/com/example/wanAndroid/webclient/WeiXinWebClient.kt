package com.example.wanAndroid.webclient

import android.content.Context
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.example.wanAndroid.util.StringUtil
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: 微信客户端
 *
 * 参考文章：https://mp.weixin.qq.com/s/gs2bojFLBB4IAWMyN9lfnw
 */
class WeiXinWebClient : BaseWebClient() {

    @Suppress("DEPRECATION")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val urlStr = url ?: ""
        if (urlStr.startsWith(WebClientFactory.WEI_XIN)) {
            val response = StringUtil.get(url ?: "")
            val res = replaceCss(response, view!!.context)
            val input = ByteArrayInputStream(res.toByteArray())
            return WebResourceResponse("text/html", "utf-8", input)
        }
        return super.shouldInterceptRequest(view, url)
    }

    private val rex = "(<style>)([\\S ]*)(</style>)"

    private fun replaceCss(res: String, context: Context): String {
        val pattern = Pattern.compile(rex)
        val m = pattern.matcher(res)
        return if (m.find()) {
            val css = StringUtil.getString(context.assets.open("weixin/weixin.css"))
            val sb = StringBuilder()
            sb.append(m.group(1))
            sb.append(css)
            sb.append(m.group(3))
            val rex = res.replace(rex.toRegex(), sb.toString())
            rex
        } else {
            res
        }
    }
}