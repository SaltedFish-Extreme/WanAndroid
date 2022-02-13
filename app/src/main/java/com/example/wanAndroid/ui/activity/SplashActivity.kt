package com.example.wanAndroid.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R

/**
 * Created by 咸鱼至尊 on 2022/2/7
 *
 * desc: 闪屏页Activity
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val layoutSplash: ConstraintLayout by lazy { findViewById(R.id.layout_splash) }
    private val alphaAnimation: AlphaAnimation by lazy { AlphaAnimation(0.3F, 1.0F) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //动画效果
        alphaAnimation.run {
            //动画持续时间
            duration = 1000
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    //执行操作
                    jumpToMain()
                }

                override fun onAnimationStart(p0: Animation?) {}
            })
        }
        //加载内容视图动画效果
        layoutSplash.startAnimation(alphaAnimation)
    }

    /** 跳转主页 */
    fun jumpToMain() {
        openActivity<MainActivity>()
        finish()
        //转场结束动画效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}