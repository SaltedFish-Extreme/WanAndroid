package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.NoDataResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.util.InputTextManager
import com.example.wanAndroid.widget.ext.hideSoftKeyboard
import com.example.wanAndroid.widget.view.SubmitButton
import com.hjq.bar.TitleBar
import com.hjq.shape.view.ShapeEditText
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.delay
import per.goweii.swipeback.SwipeBackAbility

/**
 * Created by 咸鱼至尊 on 2022/2/25
 *
 * desc: 分享文章页Activity
 */
class ShareArticleActivity : BaseActivity(), SwipeBackAbility.OnlyEdge {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val etShareTitle: ShapeEditText by lazy { findViewById(R.id.et_share_title) }
    private val etShareLink: ShapeEditText by lazy { findViewById(R.id.et_share_link) }
    private val btnShare: SubmitButton by lazy { findViewById(R.id.btn_share) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_article)
        titleBar.leftView.setOnClickListener { finish() }
        //联动分享按钮和标题链接文本框
        btnShare.let {
            InputTextManager.with(this)
                .addView(etShareTitle)
                .addView(etShareLink)
                .setMain(it)
                .build()
        }
        //分享按钮点击事件
        btnShare.setOnClickListener {
            //隐藏输入法
            hideSoftKeyboard(this)
            scopeNetLife {
                //延迟请求(因为请求速度太快，让动画效果飞一会~)
                delay(2000)
                //先获取用户登陆信息数据
                Post<ApiResponse<NoDataResponse>>(NetApi.ShareArticleAPI) {
                    param("title", etShareTitle.text.toString())
                    param("link", etShareLink.text.toString())
                }.await()
                //分享按钮显示成功
                btnShare.showSucceed()
                ToastUtils.show(getString(R.string.share_succeed))
                //再延迟一会，增强用户体验~
                delay(1000)
                //关闭当前页面
                finish()
            }.catch { //如果文章被分享过或其余错误导致请求失败会走这里
                //分享按钮显示失败
                btnShare.showError(2000)
                //弹出错误信息吐司
                ToastUtils.show(it.message)
                //标题输入框加载动画效果
                etShareTitle.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@ShareArticleActivity,
                        R.anim.shake_anim
                    )
                )
                //链接输入框加载动画效果
                etShareLink.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@ShareArticleActivity,
                        R.anim.shake_anim
                    )
                )
            }
        }
    }

    /** 只允许边缘侧滑返回 */
    override fun swipeBackOnlyEdge() = true
}