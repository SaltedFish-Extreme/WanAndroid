package com.example.wanAndroid.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ShareResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.ShareAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.decoration.SpaceItemDecoration
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.example.wanAndroid.widget.layout.SwipeItemLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.bar.TitleBar

/**
 * Created by 咸鱼至尊 on 2022/2/24
 *
 * desc: 我的分享页Activity
 */
class ShareActivity : BaseActivity() {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val page: PageRefreshLayout by lazy { findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    /** 是否初次切换页面 */
    private var first = true

    /** 数据集 */
    private lateinit var data: ApiResponse<ShareResponse>

    /** 适配器 */
    private val adapter: ShareAdapter by lazy { ShareAdapter(this) }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        //使标题栏和状态栏不重叠
        immersionBar {
            titleBar(titleBar)
        }
        titleBar.leftView.setOnClickListener { finish() }
        //标题栏右侧图标打开分享文章页面，获取返回结果，增加一条数据
        titleBar.rightView.setOnClickListener { startActivityForResult(Intent(this, ShareArticleActivity::class.java), 0, null) }
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 1
        //初始化rv
        initRV()
        //刷新数据
        onRefresh()
    }

    /** 初始化RecyclerView */
    private fun initRV() {
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //设置RecycleView的Adapter
        rv.adapter = adapter
        //设置RecycleView的分隔线
        rv.addItemDecoration(SpaceItemDecoration(this))
        //设置RecycleView的侧滑监听器
        rv.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this@ShareActivity))
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取分享列表数据
                data = Get<ApiResponse<ShareResponse>>("${NetApi.ShareListAPI}/$index/json").await()
                if (first && data.data.shareArticles.datas.isEmpty()) {
                    //如果第一次切换且数据为空显示空缺省页
                    showEmpty()
                } else {
                    //设置初次创建页面为否
                    first = false
                    index += if (index == 1) { //下拉刷新
                        //设置数据
                        adapter.setList(data.data.shareArticles.datas)
                        //翻页
                        1
                    } else { //上拉加载更多
                        if (data.data.shareArticles.datas.isNullOrEmpty()) {
                            //没有更多数据，结束动画，显示内容(没有更多数据)
                            showContent(false)
                            return@scope
                        }
                        //添加数据
                        adapter.addData(data.data.shareArticles.datas)
                        //翻页
                        1
                    }
                    showContent(true)
                }
            }
        }.refreshing()
    }

    override fun onDestroy() {
        rv.cancelFloatBtn(fab)
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            //发布文章成功后，返回当前页面刷新数据
            page.refresh()
        }
    }
}