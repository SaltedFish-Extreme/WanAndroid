package com.example.wanAndroid.webclient

import android.util.Log
import android.webkit.WebResourceResponse
import android.webkit.WebView

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: 玩安卓客户端
 *
 * 参考文章：https://mp.weixin.qq.com/s/gs2bojFLBB4IAWMyN9lfnw
 */
class WanAndroidWebClient : BaseWebClient() {
    private val articleByAuthor = "https://www.wanandroid.com/article/list/0?author="
    private val articleByCapter = "https://www.wanandroid.com/article/list_by_chapter/1"

    private val cssFiles = arrayOf(
        "blog/default.css", "blog/m_default.css", "pc/common.css",
        "pc/header.css", "wenda/wenda_md.css", "m/common.css"
    )

    @Suppress("DEPRECATION")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        cssFiles.forEach {
            if ((url ?: "").contains(it)) {
                val stream = view!!.context.assets.open("wanandroid/css/$it")
                Log.e("wanandroid", "load resource from local $it")
                return WebResourceResponse("text/css", "utf-8", stream)
            }
        }
        return super.shouldInterceptRequest(view, url)
    }
}