package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.model.CoinInfoResponse
import com.example.wanAndroid.logic.model.UserInfoResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.util.InputTextManager
import com.example.wanAndroid.widget.ext.hideSoftKeyboard
import com.example.wanAndroid.widget.view.ClearEditText
import com.example.wanAndroid.widget.view.PasswordEditText
import com.example.wanAndroid.widget.view.SubmitButton
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.delay
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.swipeback.SwipeBackDirection

/**
 * Created by 咸鱼至尊 on 2022/2/22
 *
 * desc: 注册页Activity
 */
class RegisterActivity : BaseActivity(), SwipeBackAbility.Direction {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val etUsername: ClearEditText by lazy { findViewById(R.id.et_username) }
    private val etPassword: PasswordEditText by lazy { findViewById(R.id.et_password) }
    private val etRePassword: PasswordEditText by lazy { findViewById(R.id.et_re_password) }
    private val btnRegister: SubmitButton by lazy { findViewById(R.id.btn_register) }
    private val tvLogin: TextView by lazy { findViewById(R.id.tv_login) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //联动注册按钮和账号密码输入框
        btnRegister.let {
            InputTextManager.with(this)
                .addView(etUsername)
                .addView(etPassword)
                .addView(etRePassword)
                .setMain(it)
                .build()
        }
        //注册按钮点击事件
        btnRegister.setOnClickListener {
            //校验两次密码是否一致
            if (etPassword.text.toString() != etRePassword.text.toString()) {
                etPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_anim))
                etRePassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_anim))
                btnRegister.showError(2000)
                ToastUtils.show(getString(R.string.re_enter_password))
                return@setOnClickListener
            }
            //隐藏输入法
            hideSoftKeyboard(this)
            scopeNetLife {
                //延迟请求(因为请求速度太快，让动画效果飞一会~)
                delay(1500)
                //先获取用户注册信息数据
                val userInfoData = Post<ApiResponse<UserInfoResponse>>(NetApi.RegisterAPI) {
                    param("username", etUsername.text.toString())
                    param("password", etPassword.text.toString())
                    param("repassword", etRePassword.text.toString())
                }.await()
                //再获取用户积分信息数据
                val coinInfoData = Get<ApiResponse<CoinInfoResponse>>(NetApi.CoinInfoAPI).await()
                //存储用户名
                AppConfig.UserName = userInfoData.data.username
                //存储密码
                AppConfig.PassWord = etPassword.text.toString()
                //存储用户等级
                AppConfig.Level = coinInfoData.data.level.toString()
                //存储用户排名
                AppConfig.Rank = coinInfoData.data.rank
                //存储用户积分
                AppConfig.CoinCount = coinInfoData.data.coinCount.toString()
                //注册按钮显示成功
                btnRegister.showSucceed()
                //再延迟一会，增强用户体验~
                delay(1000)
                //关闭当前页面
                finish()
            }.catch { //如果用户已注册或其余错误导致请求失败会走这里
                //注册按钮显示失败
                btnRegister.showError(2000)
                //弹出错误信息吐司
                ToastUtils.show(it.message)
                //账号输入框加载动画效果
                etUsername.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@RegisterActivity,
                        R.anim.shake_anim
                    )
                )
                //密码输入框加载动画效果
                etPassword.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@RegisterActivity,
                        R.anim.shake_anim
                    )
                )
                //确认密码输入框加载动画效果
                etRePassword.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@RegisterActivity,
                        R.anim.shake_anim
                    )
                )
            }
        }
        //登陆文本点击事件
        tvLogin.setOnClickListener {
            openActivity<LoginActivity>()
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    /** 当前页禁用侧滑 */
    override fun swipeBackDirection() = SwipeBackDirection.NONE
}