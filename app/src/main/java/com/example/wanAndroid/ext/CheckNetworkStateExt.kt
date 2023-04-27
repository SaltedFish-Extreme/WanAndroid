package com.example.wanAndroid.ext

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: 上下文对象扩展函数 网络连接检查
 *
 * @return 是否联网
 */
@Suppress("unused")
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}