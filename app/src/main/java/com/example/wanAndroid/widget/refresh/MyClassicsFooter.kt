package com.example.wanAndroid.widget.refresh

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.wanAndroid.R
import com.scwang.smart.drawable.ProgressDrawable
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

/**
 * Created by 咸鱼至尊 on 2021/12/16
 *
 * desc: 自定义加载脚
 *
 * @param context 上下文对象
 */
class MyClassicsFooter(context: Context) : SimpleComponent(context, null, 0), RefreshFooter {
    //加载文本
    private var textView: TextView

    //加载图片
    private val imageView: ImageView

    //加载进度
    private val progressDrawable = ProgressDrawable()

    //加载成功图片
    private val successDrawable: Drawable

    //加载失败图片
    private val failureDrawable: Drawable

    //默认文本
    companion object {
        private const val LOAD_FOOTER_LOADING = "正在加载"
        private const val LOAD_FOOTER_FINISH = "加载完成"
        private const val LOAD_FOOTER_FAILED = "加载失败"
        private const val LOAD_FOOTER_REFRESHING = "等待刷新"
        private const val LOAD_FOOTER_NO_MORE_DATA = "没有更多数据"
    }

    //初始化
    init {
        //设置四周边距
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10F, context.resources.displayMetrics).toInt()
        setPadding(0, padding, 0, padding)
        //填充布局
        inflate(context, R.layout.layout_my_classics_hf, this)
        //绑定标题文本图片id
        imageView = findViewById(R.id.iv_my_classics_hf_state)
        textView = findViewById(R.id.tv_my_classics_hf_content)
        //获取图片资源
        successDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_success, context.theme)!!
        failureDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_failure, context.theme)!!
        //设置文字颜色
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
        textView.setTextColor(typedValue.data)
        //设置透明度
        alpha = 0.5f
    }

    /** 加载状态改变回调 */
    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            //下拉加载
            RefreshState.Loading -> {
                //启用进度
                imageView.setImageDrawable(progressDrawable)
                progressDrawable.start()
                textView.text = LOAD_FOOTER_LOADING
            }
            //等待刷新
            RefreshState.Refreshing -> {
                //等待刷新时隐藏加载图片
                imageView.setImageDrawable(null)
                textView.text = LOAD_FOOTER_REFRESHING
            }

            else -> {}
        }
    }

    /** 加载完成回调 */
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        //停止进度
        progressDrawable.stop()
        if (success) {
            //加载成功设置成功图片
            imageView.setImageDrawable(successDrawable)
            textView.text = LOAD_FOOTER_FINISH
        } else {
            //加载失败设置失败图片
            imageView.setImageDrawable(failureDrawable)
            textView.text = LOAD_FOOTER_FAILED
        }
        //延迟200毫秒之后再弹回
        return 200
    }

    /** BRV不支持NoMoreData，使用自定义Footer时需实现setNoMoreData方法且返回true */
    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        //没有更多数据
        if (noMoreData) {
            //停止进度
            progressDrawable.stop()
            //隐藏图片
            imageView.setImageDrawable(null)
            textView.text = LOAD_FOOTER_NO_MORE_DATA
        }
        return true
    }
}