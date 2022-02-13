package com.example.wanAndroid.widget.refresh

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.wanAndroid.R
import com.jinrishici.sdk.android.JinrishiciClient
import com.jinrishici.sdk.android.listener.JinrishiciCallback
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException
import com.jinrishici.sdk.android.model.PoetySentence
import com.scwang.smart.drawable.ProgressDrawable
import com.scwang.smart.refresh.classics.ArrowDrawable
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

/**
 * Created by 咸鱼至尊 on 2021/12/15
 *
 * desc: 自定义刷新头
 *
 * @param context 上下文对象
 */
class MyClassicsHeader(context: Context) : SimpleComponent(context, null, 0), RefreshHeader {
    //刷新文本
    private var textView: TextView

    //刷新图片
    private val imageView: ImageView

    //诗词状态
    private var flag = false

    //刷新箭头
    private val arrowDrawable = ArrowDrawable()

    //刷新进度
    private val progressDrawable = ProgressDrawable()

    //刷新成功图片
    private val successDrawable: Drawable

    //刷新失败图片
    private val failureDrawable: Drawable

    //今日诗词工厂对象
    private val client by lazy { JinrishiciClient.getInstance() }

    //默认文本
    companion object {
        private const val REFRESH_HEADER_PULLING = "下拉刷新"
        private const val REFRESH_HEADER_RELEASE = "释放刷新"
        private const val REFRESH_HEADER_REFRESHING = "正在刷新"
        private const val REFRESH_HEADER_FINISH = "刷新完成"
        private const val REFRESH_HEADER_FAILED = "刷新失败"
        private const val REFRESH_HEADER_LOADING = "等待加载"
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

    /** 刷新状态改变回调 */
    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            //下拉刷新
            RefreshState.PullDownToRefresh -> {
                //设置向上箭头
                imageView.animate().rotation(0f)
                imageView.setImageDrawable(arrowDrawable)
                if (!flag) {
                    textView.text = REFRESH_HEADER_PULLING
                }
            }
            //释放刷新
            RefreshState.ReleaseToRefresh -> {
                //设置向下箭头
                imageView.animate().rotation(180f)
                imageView.setImageDrawable(arrowDrawable)
                //获取诗词
                getPoetry()
                if (!flag) {
                    textView.text = REFRESH_HEADER_RELEASE
                }
            }
            //正在刷新
            RefreshState.RefreshReleased -> {
                //取消箭头还原角度
                imageView.animate().cancel()
                imageView.rotation = 0f
                //启用进度
                imageView.setImageDrawable(progressDrawable)
                progressDrawable.start()
                if (!flag) {
                    textView.text = REFRESH_HEADER_REFRESHING
                }
            }
            //等待加载
            RefreshState.Loading -> {
                //等待加载时隐藏刷新图片
                imageView.setImageDrawable(null)
                if (!flag) {
                    textView.text = REFRESH_HEADER_LOADING
                }
            }
            else -> {}
        }
    }

    /** 刷新完成回调 */
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        //停止进度
        progressDrawable.stop()
        if (success) {
            //刷新成功设置成功图片
            imageView.setImageDrawable(successDrawable)
            if (!flag) {
                textView.text = REFRESH_HEADER_FINISH
            }
        } else {
            //刷新失败设置失败图片
            imageView.setImageDrawable(failureDrawable)
            if (!flag) {
                textView.text = REFRESH_HEADER_FAILED
            }
        }
        //延迟500毫秒之后再弹回
        return 500
    }

    /** 异步获取诗词 */
    private fun getPoetry() {
        client.getOneSentenceBackground(object : JinrishiciCallback {
            override fun done(poetySentence: PoetySentence) {
                //成功设置诗词文本(失败设置默认文本)
                textView.text = poetySentence.data.content
                //设置诗词状态true
                flag = true
            }

            override fun error(e: JinrishiciRuntimeException) {
                //设置诗词状态false，当其余状态发生改变设置默认文本
                flag = false
            }
        })
    }
}