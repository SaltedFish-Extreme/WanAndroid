package com.example.wanAndroid.widget.web

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.coolindicator.sdk.CoolIndicator
import com.example.wanAndroid.R
import com.just.agentweb.AgentWebUtils
import com.just.agentweb.BaseIndicatorView

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: 自定义彩色加载进度条
 */
class CoolIndicatorLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) :
    BaseIndicatorView(context, attrs, defStyleAttr) {

    private val mCoolIndicator by lazy { CoolIndicator.create(context as Activity) }

    init {
        mCoolIndicator.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.default_drawable_indicator, context.theme)
        this.addView(mCoolIndicator, offerLayoutParams())
    }

    override fun setProgress(newProgress: Int) {}

    override fun show() {
        this.visibility = VISIBLE
        mCoolIndicator.start()
    }

    override fun hide() {
        mCoolIndicator.complete()
    }

    override fun offerLayoutParams(): LayoutParams {
        return LayoutParams(-1, AgentWebUtils.dp2px(context, 3f))
    }
}