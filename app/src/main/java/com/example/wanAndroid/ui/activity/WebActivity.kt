package com.example.wanAndroid.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.download.library.DownloadImpl
import com.drake.serialize.intent.browse
import com.drake.serialize.intent.share
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.logic.dao.Constant
import com.example.wanAndroid.logic.dao.HistoryRecordDB
import com.example.wanAndroid.ui.BaseWebClient
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.getAgentWeb
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.ext.html2String
import com.example.wanAndroid.widget.web.WebContainer
import com.google.android.material.appbar.AppBarLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.NestedScrollAgentWebView
import com.just.agentweb.WebChromeClient
import per.goweii.swipeback.SwipeBackAbility
import java.sql.Date

/**
 * Created by 咸鱼至尊 on 2022/2/10
 *
 * desc: 网页Activity (不接收广播)
 */
//TODO 有的网页打开会闪退，暂不清楚具体原因，可能是框架问题 (E/chromium: [ERROR:aw_browser_terminator.cc(149)] Renderer process (12449) crash detected (code 11).
// A/chromium: [FATAL:crashpad_client_linux.cc(670)] Render process (12449)'s crash wasn't handled by all associated  webviews, triggering application crash.)
class WebActivity : BaseActivity(false), SwipeBackAbility.OnlyEdge {

    private val webContainer: WebContainer by lazy { findViewById(R.id.web_container) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val title: TextView by lazy { findViewById(R.id.title) }

    private lateinit var mAgentWeb: AgentWeb
    private lateinit var shareTitle: String
    private lateinit var shareUrl: String
    private var shareId = -1
    private var collect = false

    companion object {
        fun start(context: Context, id: Int, title: String, url: String, bundle: Bundle? = null) {
            Intent(context, WebActivity::class.java).run {
                putExtra(Constant.CONTENT_ID_KEY, id)
                putExtra(Constant.CONTENT_TITLE_KEY, title)
                putExtra(Constant.CONTENT_URL_KEY, url)
                context.startActivity(this, bundle)
            }
        }

        fun start(context: Context, url: String) {
            start(context, -1, "", url)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        intent.extras?.let {
            shareId = it.getInt(Constant.CONTENT_ID_KEY, -1)
            shareTitle = it.getString(Constant.CONTENT_TITLE_KEY, "")
            shareUrl = it.getString(Constant.CONTENT_URL_KEY, "")
        }
        toolbar.apply {
            //使用toolBar并使其外观与功能和actionBar一致
            setSupportActionBar(this)
            //使用默认导航按钮
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //点击导航按钮关闭当前页面
            setNavigationOnClickListener { finish() }
            //拦截导航按钮长按吐司
            navigationContentDescription = ""
            //todo 拦截溢出菜单长按吐司事件
        }
        title.apply {
            text = getString(R.string.loading)
            visibility = View.VISIBLE
            postDelayed({
                this.isSelected = true
            }, 2000)
        }
        initWebView()
    }

    /** 初始化 WebView */
    private fun initWebView() {
        val webView = NestedScrollAgentWebView(this)
        val layoutParams = CoordinatorLayout.LayoutParams(-1, -1)
        layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
        mAgentWeb = shareUrl.getAgentWeb(
            this,
            webContainer,
            layoutParams,
            webView,
            BaseWebClient(),
            mWebChromeClient
        )
        mAgentWeb.webCreator.webView.apply {
            overScrollMode = WebView.OVER_SCROLL_NEVER
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.allowFileAccess = false
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
        }
        //web文件下载监听(异步文件下载，自动申请权限，自动弹出安装)
        DownloadListener { url, _, _, _, _ ->
            //这样并不能阻止csdn等网站弹窗app下载，目前只能在BaseWebClient拦截其网址，有能力也可以加黑名单或者正则表达式判断
            if (url.startsWith("https://") && url.endsWith(".apk")) {
                DownloadImpl.getInstance(applicationContext)
                    .url(url)
                    .enqueue()
            }
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            //只对不是以URL链接为标题的网页执行以下操作(e.g.微信公众号会先显示网页链接，再显示标题)
            if (title.isNotEmpty() && !title.startsWith("http")) {
                //设置网页标题
                this@WebActivity.title.text = title.html2Spanned()
                //设置网页分享标题
                this@WebActivity.shareTitle = title.html2String()
                //设置网页分享URL
                this@WebActivity.shareUrl = view.url.toString()
                //给历史记录数据库表写入一条记录，已存在则更新
                val db = HistoryRecordDB()
                db.title = shareTitle
                db.url = shareUrl
                db.date = Date(System.currentTimeMillis())
                db.saveOrUpdate("title = ?", shareTitle)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_web, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.web_collect -> {
                //点击收藏 震动一下
                vibration()
                //收藏取反
                collect = !collect
                if (collect) {
                    //已收藏
                    item.setIcon(R.drawable.ic_collect_strawberry)
                } else {
                    //未收藏
                    item.setIcon(R.drawable.ic_collect_black_24)
                }
            }
            R.id.web_share -> {
                //分享
                share(
                    getString(R.string.web_share_url, getString(R.string.app_name), shareTitle, shareUrl),
                    getString(R.string.text_plan),
                    getString(R.string.web_share)
                )
            }
            R.id.web_refresh -> {
                //刷新网页
                mAgentWeb.urlLoader?.reload()
            }
            R.id.web_browser -> {
                //用浏览器打开
                browse(shareUrl)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mAgentWeb.run {
            if (!back()) {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        setSupportActionBar(null)
        super.onDestroy()
    }

    /** 只允许边缘侧滑返回 */
    override fun swipeBackOnlyEdge() = true
}