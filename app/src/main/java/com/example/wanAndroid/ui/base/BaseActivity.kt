package com.example.wanAndroid.ui.base

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.receiver.NetworkConnectChangedReceiver

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: Activity基类 目前只用来监听网络状态变化|状态栏沉浸|转场动画效果，可选择继承
 *
 * @property receive 是否接收广播(默认接收)
 */
open class BaseActivity(private val receive: Boolean = true) : AppCompatActivity() {

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
        if (receive) registerReceiver(mNetworkChangeListener, filter)
    }

    override fun onPause() {
        super.onPause()
        //同理，在程序暂停时销毁动态注册的广播接收器(当程序处于后台时不会接收广播)
        if (receive) unregisterReceiver(mNetworkChangeListener)
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        //转场动画效果
        overridePendingTransition(R.anim.right_in_activity, R.anim.right_out_activity)
    }

    override fun finish() {
        super.finish()
        //转场动画效果
        overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
    }
}