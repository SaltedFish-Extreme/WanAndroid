package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.appcompat.widget.SwitchCompat
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.ui.fragment.AboutDialogFragment
import com.example.wanAndroid.util.CacheDataUtil
import com.example.wanAndroid.widget.dialog.Dialog
import com.example.wanAndroid.widget.settingbar.SettingBar
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.swipeback.SwipeBackDirection

/**
 * Created by 咸鱼至尊 on 2022/2/6
 *
 * desc: 设置页Activity
 */
class SettingActivity : BaseActivity(), SwipeBackAbility.Direction {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val settingDark: SwitchCompat by lazy { findViewById(R.id.setting_dark) }
    private val settingClear: SettingBar by lazy { findViewById(R.id.setting_clear) }
    private val settingScan: SettingBar by lazy { findViewById(R.id.setting_scan) }
    private val settingUpdate: SettingBar by lazy { findViewById(R.id.setting_update) }
    private val settingWeb: SettingBar by lazy { findViewById(R.id.setting_web) }
    private val settingProject: SettingBar by lazy { findViewById(R.id.setting_project) }
    private val settingCopyright: SettingBar by lazy { findViewById(R.id.setting_copyright) }
    private val settingAbout: SettingBar by lazy { findViewById(R.id.setting_about) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //夜间模式开关是否打开
        settingDark.isChecked = AppConfig.DarkTheme
        //夜间模式开关切换监听
        settingDark.setOnCheckedChangeListener { _, isChecked ->
            AppConfig.DarkTheme = if (isChecked) {
                //当前页面设置夜间模式
                //delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                //全局设置夜间模式
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //缓存主题
                true
            } else {
                //当前页面设置日间模式
                //delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                //全局设置日间模式
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //缓存主题
                false
            }
        }
        //缓存大小文本
        settingClear.setRightText(CacheDataUtil.getTotalCacheSize(this))
        //清除缓存
        settingClear.setOnClickListener {
            Dialog.getConfirmDialog(this, getString(R.string.clear_cache)) { _, _ ->
                CacheDataUtil.clearAllCache(this)
                ToastUtils.show(getString(R.string.clear_success))
                settingClear.setRightText(CacheDataUtil.getTotalCacheSize(this))
            }.show()
        }
        //扫码下载
        settingScan.setOnClickListener { openActivity<QRCodeActivity>() }
        //版本号
        val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
        settingUpdate.setRightText(getString(R.string.setting_version, packageInfo.versionName))
        //版本更新
        settingUpdate.setOnClickListener {
            Dialog.getConfirmDialog(this, getString(R.string.share_jump)) { _, _ ->
                WebActivity.start(this, getString(R.string.update_address))
            }.show()
        }
        //官方网站
        settingWeb.setOnClickListener { WebActivity.start(this, getString(R.string.setting_site)) }
        //项目源码
        settingProject.setOnClickListener { WebActivity.start(this, getString(R.string.setting_repository)) }
        //版权声明
        settingCopyright.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.setting_copyright)
                .setMessage(R.string.copyright_content)
                .setCancelable(true)
                .show()
        }
        //关于我们
        settingAbout.setOnClickListener {
            val about = AboutDialogFragment()
            about.show(supportFragmentManager, about.tag)
        }
    }

    /** 当前页禁用侧滑 */
    override fun swipeBackDirection() = SwipeBackDirection.NONE
}