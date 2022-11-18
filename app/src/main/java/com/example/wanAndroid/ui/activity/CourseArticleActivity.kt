package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.drake.serialize.intent.bundle
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.CourseArticleResponse
import com.example.wanAndroid.logic.model.CourseResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.CourseArticleAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.example.wanAndroid.widget.layout.XCollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.bar.TitleBar

/**
 * Created by 咸鱼至尊 on 2022/11/18
 *
 * desc: 课程目录页Activity
 */
class CourseArticleActivity : BaseActivity() {

    private val collapsingToolbar: XCollapsingToolbarLayout by lazy { findViewById(R.id.collapsing_toolbar) }
    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val courseArticleImg: ImageView by lazy { findViewById(R.id.course_article_img) }
    private val courseArticleTitle: TextView by lazy { findViewById(R.id.course_article_title) }
    private val courseArticleAuthor: TextView by lazy { findViewById(R.id.course_article_author) }
    private val courseArticleDesc: TextView by lazy { findViewById(R.id.course_article_desc) }
    private val courseArticleCopyright: TextView by lazy { findViewById(R.id.course_article_copyright) }
    private val page: PageRefreshLayout by lazy { findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    /** Serialize界面传递参数: courseData */
    private val courseData: CourseResponse by bundle()

    /** 适配器 */
    private val adapter: CourseArticleAdapter by lazy { CourseArticleAdapter() }

    /** 数据集 */
    private lateinit var data: ApiResponse<ApiPagerResponse<ArrayList<CourseArticleResponse>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_article)
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //region 设置页面上要显示的数据
        courseArticleTitle.text = courseData.name
        courseArticleAuthor.text = getString(R.string.course_author, courseData.author)
        courseArticleDesc.text = courseData.desc
        courseArticleCopyright.text = courseData.lisense
        if (courseData.cover.isNotEmpty()) {
            Glide.with(this).load(courseData.cover).placeholder(R.drawable.bg_course_article).transition(DrawableTransitionOptions.withCrossFade(500))
                .into(courseArticleImg)
        } else {
            Glide.with(this).load(R.drawable.bg_course_article).into(courseArticleImg)
        }
        //endregion
        //点击版权签名，打开网页跳转对应的链接地址
        courseArticleCopyright.setOnClickListener { WebActivity.start(this, courseData.lisenseLink) }
        //初始化折叠工具栏
        initCollapsingToolbar()
        //设置RecycleView的Adapter
        rv.adapter = adapter
        //刷新数据
        onRefresh()
    }

    /** 折叠工具栏回调方法 */
    private fun initCollapsingToolbar() {
        collapsingToolbar.setOnScrimsListener(object : XCollapsingToolbarLayout.OnScrimsListener {
            override fun onScrimsStateChange(layout: XCollapsingToolbarLayout?, shown: Boolean) {
                //工具栏折叠时设置标题，隐藏用户名，否则不设置标题，显示用户名
                if (shown) {
                    titleBar.title = courseData.name
                } else {
                    titleBar.title = getString(R.string.course_catalog)
                }
            }
        })
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取课程文章列表数据
                data =
                    Get<ApiResponse<ApiPagerResponse<ArrayList<CourseArticleResponse>>>>("${NetApi.CourseCatalogListAPI}?cid=${courseData.id}&order_type=1").await()
                if (data.data.datas.isEmpty()) {
                    //如果数据为空显示空缺省页
                    showEmpty()
                } else {
                    //设置数据
                    adapter.setList(data.data.datas)
                    showContent(false)
                }
            }
        }.refreshing()
    }

    override fun onDestroy() {
        //取消rv悬浮按钮扩展函数
        rv.cancelFloatBtn(fab)
        super.onDestroy()
    }
}