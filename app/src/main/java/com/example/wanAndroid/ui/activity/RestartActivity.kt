package com.example.wanAndroid.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.base.BaseActivity
import com.hjq.toast.Toaster

/**
 * author : Android 轮子哥
 *
 * github : https://github.com/getActivity/AndroidProject-Kotlin
 *
 * time : 2020/11/29
 *
 * desc : 重启应用
 */
class RestartActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RestartActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun restart(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restart(this)
        finish()
        Toaster.show(R.string.common_crash_hint)
    }
}