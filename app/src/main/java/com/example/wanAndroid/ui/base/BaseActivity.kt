package com.example.wanAndroid.ui.base

import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.wanAndroid.ui.receiver.NetworkConnectChangedReceiver

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: Activity基类 目前只用来监听网络状态变化及状态栏沉浸，可选择继承
 */
open class BaseActivity : AppCompatActivity() {

    //延迟初始化网络状态变化监听器
    private val mNetworkChangeListener by lazy { NetworkConnectChangedReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使页面填充到状态栏(为了让toolBar沉浸状态栏)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onResume() {
        super.onResume()
        //创建意图过滤器对象
        val filter = IntentFilter()
        //添加网络状态变化意图
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        //在程序恢复时动态注册广播接收器，确保只有处于栈顶的activity可以接收广播
        registerReceiver(mNetworkChangeListener, filter)
    }

    override fun onPause() {
        super.onPause()
        //同理，在程序暂停时销毁动态注册的广播接收器(当程序处于后台时不会接收广播)
        unregisterReceiver(mNetworkChangeListener)
    }
}