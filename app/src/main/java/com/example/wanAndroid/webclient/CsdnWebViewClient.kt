package com.example.wanAndroid.webclient

import android.webkit.WebResourceResponse
import android.webkit.WebView

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: CSDNweb客户端
 *
 * 参考文章：https://mp.weixin.qq.com/s/gs2bojFLBB4IAWMyN9lfnw
 */
class CsdnWebViewClient : BaseWebClient() {
    @Suppress("DEPRECATION")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val urlStr = url ?: ""
        if (urlStr.endsWith(".css")) {
            if (urlStr.startsWith("https://csdnimg.cn/release/phoenix/production/mobile_detail_style")) {
                val stream = view!!.context.assets.open("csdn/mobile_detail.css")
                return WebResourceResponse("text/css", "utf-8", stream)
            }
            if (urlStr.startsWith("https://csdnimg.cn/release/phoenix/production/wapedit_views_md")) {
                val stream = view!!.context.assets.open("csdn/md.css")
                return WebResourceResponse("text/css", "utf-8", stream)
            }
            if (urlStr.startsWith("https://csdnimg.cn/release/phoenix/template")) {
                if (urlStr.contains("wap_detail_view") && urlStr.endsWith(".css")) {
                    val stream = view!!.context.assets.open("csdn/wap_detail_view.css")
                    return WebResourceResponse("text/css", "utf-8", stream)
                }
            }
        }
        return super.shouldInterceptRequest(view, url)
    }
}