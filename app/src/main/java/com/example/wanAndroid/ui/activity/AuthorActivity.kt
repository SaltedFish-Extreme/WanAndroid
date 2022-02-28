package com.example.wanAndroid.ui.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.drake.net.utils.scopeNetLife
import com.drake.serialize.intent.bundle
import com.example.wanAndroid.MyApplication
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.logic.model.ShareResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.ArticleAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.example.wanAndroid.widget.layout.XCollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.bar.TitleBar
import java.io.File

/**
 * Created by 咸鱼至尊 on 2022/2/14
 *
 * desc: 作者页Activity
 */
class AuthorActivity : BaseActivity() {

    private val collapsingToolbar: XCollapsingToolbarLayout by lazy { findViewById(R.id.collapsing_toolbar) }
    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val headerImage: ImageView by lazy { findViewById(R.id.header_image) }
    private val headerText: TextView by lazy { findViewById(R.id.header_text) }
    private val page: PageRefreshLayout by lazy { findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    /** Serialize界面传递参数: name */
    private val name: String by bundle()

    /** Serialize界面传递参数: userId */
    private val userId: Int by bundle()

    /** 是否初次切换页面 */
    private var first = true

    /** 适配器 */
    private val adapter: ArticleAdapter by lazy { ArticleAdapter(this) }

    /** 按照作者昵称搜索文章 数据集 */
    private lateinit var dataByName: ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>

    /** 分享人对应列表数据 数据集 */
    private lateinit var dataByID: ApiResponse<ShareResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author)
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //是否作者(userid为-1)
        if (userId == -1) {
            //设置此页面请求分页初始索引(按照作者昵称搜索文章)
            PageRefreshLayout.startIndex = 0
        } else {
            //设置此页面请求分页初始索引(分享人对应列表数据)
            PageRefreshLayout.startIndex = 1
        }
        //用户名
        headerText.text = name
        //用户头像
        loadHeaderImage()
        //初始化折叠工具栏
        initCollapsingToolbar()
        //设置RecycleView的Adapter
        rv.adapter = adapter
        //刷新数据
        onRefresh()
    }

    override fun onDestroy() {
        //取消rv悬浮按钮扩展函数
        rv.cancelFloatBtn(fab)
        super.onDestroy()
    }

    /** 加载头像 */
    private fun loadHeaderImage() {
        scopeNetLife {
            //请求头像图片并下载生成文件
            val mAvatar = Get<File>("${NetApi.GenerateAvatarAPI}/$name.png?apikey=${MyApplication.apikey}") {
                setDownloadDir(MyApplication.context.filesDir)
                setDownloadMd5Verify()
                setDownloadFileNameDecode()
                setDownloadTempFile()
            }.await()
            //文件解码bitmap
            val mBitmap = BitmapFactory.decodeFile(mAvatar.path)
            //设置头像
            headerImage.setImageBitmap(mBitmap)
        }
    }

    /** 折叠工具栏回调方法 */
    private fun initCollapsingToolbar() {
        collapsingToolbar.setOnScrimsListener(object : XCollapsingToolbarLayout.OnScrimsListener {
            override fun onScrimsStateChange(layout: XCollapsingToolbarLayout?, shown: Boolean) {
                //工具栏折叠时设置标题，隐藏用户名，否则不设置标题，显示用户名
                if (shown) {
                    titleBar.title = name
                    headerText.visibility = View.INVISIBLE
                } else {
                    titleBar.title = ""
                    headerText.visibility = View.VISIBLE
                }
            }
        })
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //是否作者(userid为-1)
                if (userId == -1) {
                    //按照作者昵称搜索文章
                    dataByName =
                        Get<ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>>("${NetApi.SearchArticleByNameAPI}/$index/json?author=$name").await()
                    if (first && dataByName.data.datas.isEmpty()) {
                        //如果第一次切换且数据为空显示空缺省页
                        showEmpty()
                    } else {
                        //设置初次创建页面为否
                        first = false
                        index += if (index == 0) { //下拉刷新
                            //设置数据
                            adapter.setList(dataByName.data.datas)
                            //翻页
                            1
                        } else { //上拉加载更多
                            if (dataByName.data.datas.isNullOrEmpty()) {
                                //没有更多数据，结束动画，显示内容(没有更多数据)
                                showContent(false)
                                return@scope
                            }
                            //添加数据
                            adapter.addData(dataByName.data.datas)
                            //翻页
                            1
                        }
                        showContent(true)
                    }
                } else {
                    //分享人对应列表数据
                    dataByID =
                        Get<ApiResponse<ShareResponse>>("${NetApi.SearchArticleByIdAPI}/$userId/share_articles/$index/json").await()
                    if (first && dataByID.data.shareArticles.datas.isEmpty()) {
                        //如果第一次切换且数据为空显示空缺省页
                        showEmpty()
                    } else {
                        //设置初次创建页面为否
                        first = false
                        index += if (index == 1) { //下拉刷新
                            //设置数据
                            adapter.setList(dataByID.data.shareArticles.datas)
                            //翻页
                            1
                        } else { //上拉加载更多
                            if (dataByID.data.shareArticles.datas.isNullOrEmpty()) {
                                //没有更多数据，结束动画，显示内容(没有更多数据)
                                showContent(false)
                                return@scope
                            }
                            //添加数据
                            adapter.addData(dataByID.data.shareArticles.datas)
                            //翻页
                            1
                        }
                        showContent(true)
                    }
                }
            }
        }.refreshing()
    }
}