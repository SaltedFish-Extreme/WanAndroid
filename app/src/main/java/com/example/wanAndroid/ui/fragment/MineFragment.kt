package com.example.wanAndroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.model.NoDataResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.activity.*
import com.example.wanAndroid.widget.dialog.Dialog
import com.example.wanAndroid.widget.settingbar.SettingBar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.hjq.toast.Toaster

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 我的Fragment
 */
class MineFragment : Fragment() {

    private val rankImage: ImageView by lazy { requireView().findViewById(R.id.rank_image) }
    private val headerImage: ShapeableImageView by lazy { requireView().findViewById(R.id.header_image) }
    private val userText: TextView by lazy { requireView().findViewById(R.id.user_text) }
    private val levelText: TextView by lazy { requireView().findViewById(R.id.level_text) }
    private val rankText: TextView by lazy { requireView().findViewById(R.id.rank_text) }
    private val mineIntegral: SettingBar by lazy { requireView().findViewById(R.id.mine_integral) }
    private val mineCollect: SettingBar by lazy { requireView().findViewById(R.id.mine_collect) }
    private val mineShare: SettingBar by lazy { requireView().findViewById(R.id.mine_share) }
    private val mineRecord: SettingBar by lazy { requireView().findViewById(R.id.mine_record) }
    private val mineSetting: SettingBar by lazy { requireView().findViewById(R.id.mine_setting) }
    private val mineExit: SettingBar by lazy { requireView().findViewById(R.id.mine_exit) }
    private val toolbar: MaterialToolbar by lazy { requireActivity().findViewById(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //控制选项菜单
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //未登陆过则点击头像跳转登陆页面并获取返回结果
        if (AppConfig.UserName.isEmpty()) {
            headerImage.setOnClickListener {
                startActivityForResult(Intent(context, LoginActivity::class.java), 0, null)
            }
        }
        rankImage.setOnClickListener { openActivity<LeaderboardActivity>() }
        mineIntegral.setOnClickListener {
            if (AppConfig.UserName.isEmpty()) {
                Toaster.show(getString(R.string.please_login))
                startActivityForResult(Intent(context, LoginActivity::class.java), 0, null)
            } else {
                openActivity<IntegralActivity>()
            }
        }
        mineCollect.setOnClickListener {
            if (AppConfig.UserName.isEmpty()) {
                Toaster.show(getString(R.string.please_login))
                startActivityForResult(Intent(context, LoginActivity::class.java), 0, null)
            } else {
                openActivity<CollectActivity>()
            }
        }
        mineShare.setOnClickListener {
            if (AppConfig.UserName.isEmpty()) {
                Toaster.show(getString(R.string.please_login))
                startActivityForResult(Intent(context, LoginActivity::class.java), 0, null)
            } else {
                openActivity<ShareActivity>()
            }
        }
        mineRecord.setOnClickListener { openActivity<HistoryRecordActivity>() }
        mineSetting.setOnClickListener { openActivity<SettingActivity>() }
        //未登录隐藏登出项，登陆可见
        mineExit.isVisible = AppConfig.CoinCount.isNotEmpty()
        mineExit.setOnClickListener {
            //登出弹窗确认
            Dialog.getConfirmDialog(context!!, getString(R.string.exit_confirm)) { _, _ ->
                scopeNetLife {
                    //退出清除cookie
                    Get<NoDataResponse>(NetApi.ExitAPI).await()
                }
                //从存储中清除cookie、个人信息
                AppConfig.Cookie.clear()
                AppConfig.UserName = ""
                AppConfig.PassWord = ""
                AppConfig.Level = ""
                AppConfig.Rank = ""
                AppConfig.CoinCount = ""
                Toaster.show(getString(R.string.exit_succeed))
                //重建页面
                activity?.recreate()
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = getString(R.string.mine_fragment)
        //用户名
        userText.text = AppConfig.UserName.ifEmpty { getString(R.string.my_user) }
        //等级文字
        levelText.text = AppConfig.Level.ifEmpty { getString(R.string.my_ellipsis) }
        //排名文字
        rankText.text = AppConfig.Rank.ifEmpty { getString(R.string.my_ellipsis) }
        //积分项设置文本
        mineIntegral.setRightText(AppConfig.CoinCount.ifEmpty { "" })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        //清除menu
        //menu.clear()
        //隐藏toolbar右侧显示的menu
        menu.findItem(R.id.search).isVisible = false
    }
}