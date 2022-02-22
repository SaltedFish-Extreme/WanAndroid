package com.example.wanAndroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.ui.activity.HistoryRecordActivity
import com.example.wanAndroid.ui.activity.LoginActivity
import com.example.wanAndroid.ui.activity.SettingActivity
import com.example.wanAndroid.widget.settingbar.SettingBar
import com.example.wanAndroid.widget.toolbar.Toolbar
import com.google.android.material.imageview.ShapeableImageView
import com.hjq.toast.ToastUtils

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 我的Fragment
 */
class MineFragment : Fragment() {

    private val notificationImage: ImageView by lazy { requireView().findViewById(R.id.notification_image) }
    private val headerImage: ShapeableImageView by lazy { requireView().findViewById(R.id.header_image) }
    private val userText: TextView by lazy { requireView().findViewById(R.id.user_text) }
    private val gradeText: TextView by lazy { requireView().findViewById(R.id.grade_text) }
    private val rankText: TextView by lazy { requireView().findViewById(R.id.rank_text) }
    private val mineIntegral: SettingBar by lazy { requireView().findViewById(R.id.mine_integral) }
    private val mineCollect: SettingBar by lazy { requireView().findViewById(R.id.mine_collect) }
    private val mineShare: SettingBar by lazy { requireView().findViewById(R.id.mine_share) }
    private val mineRecord: SettingBar by lazy { requireView().findViewById(R.id.mine_record) }
    private val mineSetting: SettingBar by lazy { requireView().findViewById(R.id.mine_setting) }
    private val mineExit: SettingBar by lazy { requireView().findViewById(R.id.mine_exit) }
    private val toolbar: Toolbar by lazy { requireActivity().findViewById(R.id.toolbar) }

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
        //用户名存储过则设置
        userText.text = AppConfig.UserName.ifEmpty { getString(R.string.my_user) }
        gradeText.text = getString(R.string.my_score)
        rankText.text = getString(R.string.my_score)
        mineIntegral.setRightText(R.string.my_score)
        mineIntegral.setOnClickListener { ToastUtils.debugShow(R.string.my_integral) }
        mineCollect.setOnClickListener { ToastUtils.debugShow(R.string.my_collect) }
        mineShare.setOnClickListener { ToastUtils.debugShow(R.string.my_share) }
        mineRecord.setOnClickListener { openActivity<HistoryRecordActivity>() }
        mineSetting.setOnClickListener { openActivity<SettingActivity>() }
        mineExit.setOnClickListener { ToastUtils.debugShow(R.string.my_exit) }
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = getString(R.string.mine_fragment)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        //清除menu
        //menu.clear()
        //隐藏toolbar右侧显示的menu
        menu.findItem(R.id.search).isVisible = false
    }
}