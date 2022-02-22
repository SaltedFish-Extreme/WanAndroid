package com.example.wanAndroid.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.navigation.fragment.NavHostFragment
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.model.CoinInfoResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.interceptLongClick
import com.example.wanAndroid.widget.toolbar.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.hjq.toast.ToastUtils
import org.litepal.LitePal
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.swipeback.SwipeBackDirection

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: 主页Activity
 */
class MainActivity : BaseActivity(), SwipeBackAbility.Direction {

    private val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawer_layout) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val bottomNavigationView: BottomNavigationView by lazy { findViewById(R.id.bottom_navigation_view) }
    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navView: NavigationView by lazy { findViewById(R.id.nav_view) }

    /** 退出时间 */
    private var exitTime = 0L

    /** 数据集 */
    private lateinit var data: ApiResponse<CoinInfoResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //使用toolBar并使其外观与功能和actionBar一致
        setSupportActionBar(toolbar)
        //初始化抽屉布局控件
        initDrawerLayout()
        //初始化底部导航栏控件
        initBottomNavigationView()
        //初始化侧滑栏控件
        initNavigationView()
        //获取数据库
        LitePal.getDatabase()
        //删除数据库
        //LitePal.deleteDatabase("example")
        //获取积分信息
        getCoinInfo()
    }

    /** 初始化抽屉布局控件 */
    private fun initDrawerLayout() {
        //开关抽屉时导航按钮的旋转动画效果
        drawerLayout.run {
            val toggle = ActionBarDrawerToggle(this@MainActivity, this, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            addDrawerListener(toggle)
            toggle.syncState()
        }
        //抽屉视图仿QQ侧滑栏效果
        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //滑动过程中不断回调 slideOffset:0~1
                val content: View = drawerLayout.getChildAt(0)
                val menu: View = drawerView
                val scale = 1 - slideOffset //1~0
                content.translationX = menu.measuredWidth * (1 - scale) //0~width
            }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    /** 初始化底部导航栏控件 */
    private fun initBottomNavigationView() {
        //region 注释无用收起方法
        //让BottomNavigationView与NavController相关联
        //fragment的重复加载问题和NavController有关
        //为了不在切换选项卡时重建fragment，这里不使用bottomNavigationView.setupWithNavController方法
        /*方法一：在fragment管理器里通过id找到NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottomNavigationView.setupWithNavController(navHostFragment.navController)*/
        /*方法二：把xml中FragmentContainerView改成fragment
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)*/
        //endregion
        //拦截底部导航栏长按弹吐司事件
        bottomNavigationView.interceptLongClick()
        //自定义底部导航栏切换选项卡时的导航操作
        bottomNavigationView.setOnItemSelectedListener {
            navHostFragment.navController.navigate(it.itemId)
            return@setOnItemSelectedListener true
        }
    }

    /** 初始化侧滑栏控件 */
    @Suppress("DEPRECATION")
    private fun initNavigationView() {
        //侧滑栏头布局
        navView.getHeaderView(0).run {
            //积分排名图标
            val rankImage = findViewById<ImageView>(R.id.rank_image)
            //用户头像
            val headerImage = findViewById<ShapeableImageView>(R.id.header_image)
            //用户名
            findViewById<TextView>(R.id.user_text).text = AppConfig.UserName.ifEmpty { getString(R.string.my_user) }
            //等级文字
            findViewById<TextView>(R.id.level_text).text = AppConfig.Level.ifEmpty { getString(R.string.my_ellipsis) }
            //排名文字
            findViewById<TextView>(R.id.rank_text).text = AppConfig.Rank.ifEmpty { getString(R.string.my_ellipsis) }
            //积分项设置文本
            val navMenu = navView.menu.findItem(R.id.nav_integral).actionView as TextView
            navMenu.gravity = Gravity.CENTER_VERTICAL
            navMenu.text = AppConfig.CoinCount.ifEmpty { "" }
            //未登陆过则点击头像跳转登陆页面并获取返回结果
            if (AppConfig.UserName.isEmpty()) {
                headerImage.setOnClickListener {
                    startActivityForResult(Intent(context, LoginActivity::class.java), 0, null)
                }
            }
            //积分图标点击事件
            rankImage.setOnClickListener { ToastUtils.debugShow("积分排名") }
        }
        //未登录隐藏登出项
        //navView.menu.findItem(R.id.nav_exit).isVisible = false
        //侧滑栏菜单项点击事件监听
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_integral -> ToastUtils.debugShow(R.string.my_integral)
                R.id.nav_collect -> ToastUtils.debugShow(R.string.my_collect)
                R.id.nav_share -> ToastUtils.debugShow(R.string.my_share)
                R.id.nav_record -> openActivity<HistoryRecordActivity>()
                R.id.nav_setting -> openActivity<SettingActivity>()
                R.id.nav_exit -> ToastUtils.debugShow(R.string.my_exit)
            }
            true
        }
    }

    /** 获取积分数据 */
    private fun getCoinInfo() {
        if (AppConfig.UserName.isNotEmpty()) {
            scopeNetLife {
                data = Get<ApiResponse<CoinInfoResponse>>(NetApi.CoinInfoAPI).await()
                AppConfig.Level = data.data.level.toString()
                AppConfig.Rank = data.data.rank
                AppConfig.CoinCount = data.data.coinCount.toString()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //填充toolbar右侧menu
        menuInflater.inflate(R.menu.menu_toolbar_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //点击搜索按钮跳转搜索页面
            R.id.search -> openActivity<SearchActivity>()
        }
        return true
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //从登陆页面返回，已登陆，重建activity刷新数据
        if (AppConfig.UserName.isNotEmpty()) {
            recreate()
        }
    }

    override fun onBackPressed() {
        //返回键退出程序确认
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.show(getString(R.string.exit))
            exitTime = System.currentTimeMillis()
            return
        }
        super.onBackPressed()
    }

    /** 当前页禁用侧滑 */
    override fun swipeBackDirection() = SwipeBackDirection.NONE
}