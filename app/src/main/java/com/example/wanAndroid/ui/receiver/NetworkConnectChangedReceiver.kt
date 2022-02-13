package com.example.wanAndroid.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import com.example.wanAndroid.R
import com.hjq.toast.ToastUtils

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * 网络变化监听广播
 *
 * desc: 监听网络状态的改变
 */
class NetworkConnectChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //获取网络连接管理器
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        /** 延迟加载需 api26 安卓8 及以上可用 不延迟则必现bug: 断网开app不会走回调，联网打开没问题 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //请求网络状态 NetworkRequest(网络请求构建器，网络请求回调接口，延迟毫秒加载)
            manager.requestNetwork(NetworkRequest.Builder().build(), object : ConnectivityManager.NetworkCallback() {

                //连接可用
                /*override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    ToastUtils.show("当前网络连接可用")
                }*/

                //连接不可用
                override fun onUnavailable() {
                    super.onUnavailable()
                    ToastUtils.show(R.string.no_wifi)
                }

                //region 注释丢失连接回调
                /*//丢失连接
                // 和onUnavailable方法作用一样，只是单独使用这个就算设置延迟也会出必现bug，而单独用onUnavailable不会出现
                // 当wifi和流量同时打开时，先关闭wifi会先走这个回调再走onAvailable，onUnavailable不会走，先关闭流量都不会走
                // 可以和onUnavailable同时使用，也可以单独使用onUnavailable，不推荐单独使用这个
                override fun onLost(network: Network) {
                    super.onLost(network)
                    ToastUtils.show(R.string.no_wifi)
                }*/
                //endregion

            }, 500)
        }

        //region 注释封装的网络连接检查工具类 没有版本限制
        /*//获取网络活动对象
        val nw = manager.activeNetwork
        //获取网络连接状态
        val actNw = manager.getNetworkCapabilities(nw)
        actNw?.let {
            //获取成功，遍历状态
            when {
                //WIFI传输可用
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> KLog.e("当前网络连接可用")
                //蜂窝网络传输可用
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> KLog.e("当前网络连接可用")
                //以太网传输可用
                it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> KLog.e("当前网络连接可用")
                //蓝牙传输可用
                it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> KLog.e("当前网络连接可用")
                //否则网络连接失败
                else -> KLog.e(R.string.no_wifi)
            }
        } ?: run {
            //获取网络连接状态失败
            KLog.e(R.string.no_wifi)
        }*/
        //endregion

        //region 注释可用已过时方法: api29 安卓10
        /*val activeNetwork = manager.activeNetworkInfo
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.isConnected) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    //connected to wifi
                    KLog.e("当前WiFi连接可用")
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    //connected to mobile
                    KLog.e("当前移动网络连接可用")
                }
            } else {
                KLog.e(R.string.no_wifi)
            }
        } else {
            //not connected to the internet
            KLog.e(R.string.no_wifi)
        }*/
        //endregion
    }
}