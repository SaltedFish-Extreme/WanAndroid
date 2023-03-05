package com.example.wanAndroid.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.core.content.ContextCompat
import com.download.library.DownloadImpl
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.dao.Constant
import com.example.wanAndroid.logic.dao.HistoryRecordDB
import com.example.wanAndroid.logic.model.CollectResponse
import com.example.wanAndroid.logic.model.NoDataResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.BaseWebClient
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.getAgentWeb
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.ext.html2String
import com.example.wanAndroid.widget.web.WebContainer
import com.google.android.material.appbar.AppBarLayout
import com.hjq.toast.Toaster
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
    private var isCollect = false
    private var isArticle = true
    private var originId = -1
    private var data: CollectResponse? = null

    companion object {

        /**
         * 从文章打开网页
         *
         * @param context 上下文对象
         * @param id 文章ID
         * @param title 文章标题
         * @param url 文章URL
         * @param collect 是否收藏
         * @param article 是否文章(不用填)
         * @param originId 文章原始ID(收藏页面跳转时使用，网页取消收藏时同时从收藏列表中删除)
         * @param data 数据源(收藏数据类，网页取消收藏时同时从收藏列表中删除)
         */
        fun start(
            context: Context,
            id: Int,
            title: String,
            url: String,
            collect: Boolean = false,
            article: Boolean = true,
            originId: Int = -1,
            data: CollectResponse? = null
        ) {
            Intent(context, WebActivity::class.java).run {
                putExtra(Constant.CONTENT_ID_KEY, id)
                putExtra(Constant.CONTENT_ORIGIN_ID_KEY, originId)
                putExtra(Constant.CONTENT_TITLE_KEY, title)
                putExtra(Constant.CONTENT_URL_KEY, url)
                putExtra(Constant.CONTENT_COLLECT_KEY, collect)
                putExtra(Constant.CONTENT_ARTICLE_KEY, article)
                putExtra(Constant.CONTENT_DATA_KEY, data)
                context.startActivity(this)
            }
        }

        /**
         * 普通打开网页
         *
         * @param context 上下文对象
         * @param url 网址URL
         */
        fun start(context: Context, url: String) {
            //不是从文章进来
            start(context, -1, "", url, article = false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        intent.extras?.let {
            shareId = it.getInt(Constant.CONTENT_ID_KEY, -1)
            originId = it.getInt(Constant.CONTENT_ORIGIN_ID_KEY, -1)
            shareTitle = it.getString(Constant.CONTENT_TITLE_KEY, "")
            shareUrl = it.getString(Constant.CONTENT_URL_KEY, "")
            isCollect = it.getBoolean(Constant.CONTENT_COLLECT_KEY, false)
            isArticle = it.getBoolean(Constant.CONTENT_ARTICLE_KEY, true)
            data = it.getParcelable(Constant.CONTENT_DATA_KEY)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_web, menu)
        if (!isArticle) {
            //不是从文章进来，隐藏收藏选项
            menu?.findItem(R.id.web_collect)?.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //如果收藏了，右上角的收藏图标相对应改变
        this.let {
            if (isCollect) {
                //已收藏
                menu.findItem(R.id.web_collect).icon = ContextCompat.getDrawable(it, R.drawable.ic_collect_strawberry)
            } else {
                //未收藏
                menu.findItem(R.id.web_collect).icon = ContextCompat.getDrawable(it, R.drawable.ic_collect_black_24)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.web_collect -> {
                if (AppConfig.UserName.isEmpty()) {
                    Toaster.show(getString(R.string.please_login))
                    return true
                }
                //点击收藏 震动一下
                vibration()
                //收藏取反
                isCollect = !isCollect
                if (isCollect) {
                    //收藏
                    item.setIcon(R.drawable.ic_collect_strawberry)
                    scopeNetLife {
                        if (originId == -1) {
                            //不是从收藏列表进来，根据文章id收藏
                            Post<NoDataResponse>("${NetApi.CollectArticleAPI}/$shareId/json").await()
                        } else {
                            //从收藏列表进来，根据文章原始id收藏
                            Post<NoDataResponse>("${NetApi.CollectArticleAPI}/$originId/json").await()
                        }
                    }
                } else {
                    //取消收藏
                    item.setIcon(R.drawable.ic_collect_black_24)
                    scopeNetLife {
                        if (originId == -1) {
                            //不是从收藏列表进来，从文章列表删除
                            Post<NoDataResponse>("${NetApi.UnCollectArticleAPI}/$shareId/json").await()
                        } else {
                            //从收藏列表进来，从收藏列表删除
                            Post<NoDataResponse>("${NetApi.UserUnCollectArticleAPI}/$shareId/json") {
                                param("originId", "$originId")
                            }.await()
                        }
                    }
                }
            }
            R.id.web_share -> {
                //分享
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_plan))
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.web_share_url, getString(R.string.app_name), shareTitle, shareUrl))
                startActivity(Intent.createChooser(intent, getString(R.string.web_share)))
            }
            R.id.web_refresh -> {
                //刷新网页
                mAgentWeb.urlLoader?.reload()
            }
            R.id.web_browser -> {
                //用浏览器打开
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(shareUrl)))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
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
        if (originId != -1 && !isCollect) {
            //从收藏列表进来，并且最后取消收藏，则发送事件同步收藏列表(删除这条item)
            data?.let { sendEvent(it) }
        } else {
            //如果收藏，发送标签(true)
            if (isCollect) {
                sendTag(true.toString())
            } else {
                //如果未收藏发送标签(false)
                sendTag(false.toString())
            }
        }
        mAgentWeb.webLifeCycle.onDestroy()
        setSupportActionBar(null)
        super.onDestroy()
    }

    /** 只允许边缘侧滑返回 */
    override fun swipeBackOnlyEdge() = true
}