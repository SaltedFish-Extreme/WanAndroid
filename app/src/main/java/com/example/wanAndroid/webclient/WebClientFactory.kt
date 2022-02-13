package com.example.wanAndroid.webclient

import com.just.agentweb.WebViewClient

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: web客户端工厂类
 */
object WebClientFactory {

    const val WAN_ANDROID = "wanandroid.com"
    const val JIAN_SHU = "https://www.jianshu.com"
    const val JUE_JIN = "https://juejin.cn"
    const val WEI_XIN = "https://mp.weixin.qq.com"
    const val CSDN = "blog.csdn.net"

    fun create(url: String): WebViewClient {
        return when {
            url.contains(WAN_ANDROID) -> WanAndroidWebClient()
            url.startsWith(JIAN_SHU) -> JianShuWebClient()
            url.startsWith(JUE_JIN) -> JueJinWebClient()
            url.startsWith(WEI_XIN) -> WeiXinWebClient()
            url.contains(CSDN) -> CsdnWebViewClient()
            else -> BaseWebClient()
        }
    }
}