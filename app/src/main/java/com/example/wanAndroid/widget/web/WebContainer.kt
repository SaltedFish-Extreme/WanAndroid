package com.example.wanAndroid.widget.web

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.example.wanAndroid.R
import com.example.wanAndroid.widget.ext.alphaColor

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: web容器视图(暗色滤镜)
 */
class WebContainer : CoordinatorLayout {

    private val mMaskColor by lazy { alphaColor(ContextCompat.getColor(context, R.color.color_web_bg_draw), 0.2f) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawColor(mMaskColor)
    }
}