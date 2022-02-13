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
 * desc: 简书web客户端
 *
 * 参考文章：https://mp.weixin.qq.com/s/gs2bojFLBB4IAWMyN9lfnw
 */
class JianShuWebClient : BaseWebClient() {

    private val rex = "(<style data-vue-ssr-id=[\\s\\S]*?>)([\\s\\S]*]?)(</style>)"

    private val bodyRex = "<body class=\"([\\ss\\S]*?)\""

    @Suppress("DEPRECATION")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val urlStr = url ?: ""
        if (urlStr.startsWith(WebClientFactory.JIAN_SHU)) {
            val response = StringUtil.get(url ?: "")
            val res = darkBody(replaceCss(response, view!!.context))
            val input = ByteArrayInputStream(res.toByteArray())
            return WebResourceResponse("text/html", "utf-8", input)
        }
        return super.shouldInterceptRequest(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val script = """
            console.log("jianshu script...");
            var imgs = document.getElementsByClassName("image-loading");
            for(var i=0;i<imgs.length;i++){
                imgs[i].src = imgs[i].dataset.originalSrc;
                imgs[i].className="";
            }
        """.trimIndent()
        view?.evaluateJavascript(script) {}
    }

    private fun darkBody(res: String): String {
        val pattern = Pattern.compile(bodyRex)
        val m = pattern.matcher(res)
        return if (m.find()) {
            val s = "<body class=\"reader-night-mode normal-size\""
            res.replace(bodyRex.toRegex(), s)
        } else res
    }

    private fun replaceCss(res: String, context: Context): String {
        val pattern = Pattern.compile(rex)
        val m = pattern.matcher(res)
        return if (m.find()) {
            val css = StringUtil.getString(context.assets.open("jianshu/jianshu.css"))
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