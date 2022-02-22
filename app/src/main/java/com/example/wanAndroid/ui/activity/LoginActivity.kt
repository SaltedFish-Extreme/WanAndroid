package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.drake.net.Post
import com.drake.net.utils.scope
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
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
 * Created by 咸鱼至尊 on 2022/2/20
 *
 * desc: 登陆页Activity
 */
class LoginActivity : BaseActivity(), SwipeBackAbility.Direction {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val etUsername: ClearEditText by lazy { findViewById(R.id.et_username) }
    private val etPassword: PasswordEditText by lazy { findViewById(R.id.et_password) }
    private val btnLogin: SubmitButton by lazy { findViewById(R.id.btn_login) }
    private val tvRegister: TextView by lazy { findViewById(R.id.tv_register) }

    /** 数据集 */
    private lateinit var data: ApiResponse<UserInfoResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //联动登陆按钮和账号密码输入框
        btnLogin.let {
            InputTextManager.with(this)
                .addView(etUsername)
                .addView(etPassword)
                .setMain(it)
                .build()
        }
        //登陆按钮点击事件
        btnLogin.setOnClickListener {
            //隐藏输入法
            hideSoftKeyboard(this)
            scope {
                //延迟请求(因为请求速度太快，让动画效果飞一会~)
                delay(1500)
                //获取用户信息数据
                data = Post<ApiResponse<UserInfoResponse>>(NetApi.LoginAPI) {
                    param("username", etUsername.text.toString())
                    param("password", etPassword.text.toString())
                }.await()
                //存储用户名
                AppConfig.UserName = data.data.username
                //登陆按钮显示成功
                btnLogin.showSucceed()
                //再延迟一会，增强用户体验~
                delay(1000)
                //关闭当前页面
                finish()
            }.catch {
                //如果用户名或密码错误导致请求失败会走这里
                //登陆按钮显示失败
                btnLogin.showError(2000)
                //账号输入框加载动画效果
                etUsername.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@LoginActivity,
                        R.anim.shake_anim
                    )
                )
                //密码输入框加载动画效果
                etPassword.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@LoginActivity,
                        R.anim.shake_anim
                    )
                )
                //弹出错误信息吐司
                ToastUtils.show(it.message)
            }
        }
        //注册文本点击事件
        tvRegister.setOnClickListener { ToastUtils.debugShow("OK") }
    }

    /** 当前页禁用侧滑 */
    override fun swipeBackDirection() = SwipeBackDirection.NONE
}