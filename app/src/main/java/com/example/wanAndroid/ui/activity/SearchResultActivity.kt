package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Post
import com.drake.net.utils.scope
import com.drake.serialize.intent.bundle
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.ArticleAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.bar.TitleBar

/**
 * Created by 咸鱼至尊 on 2022/2/18
 *
 * desc: 搜索结果页Activity
 */
class SearchResultActivity : BaseActivity() {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val page: PageRefreshLayout by lazy { findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    /** 是否初次切换页面 */
    private var first = true

    /** 适配器 */
    private val adapter: ArticleAdapter by lazy { ArticleAdapter() }

    /** 数据集 */
    private lateinit var data: ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>

    /** Serialize界面传递参数: key */
    private val key: String by bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 0
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //设置标题
        titleBar.title = key
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //刷新数据
        onRefresh()
        //设置RecycleView的Adapter
        rv.adapter = adapter
    }

    override fun onDestroy() {
        rv.cancelFloatBtn(fab)
        super.onDestroy()
    }

    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取搜索结果数据
                data = Post<ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>>("${NetApi.SearchResultAPI}/$index/json") {
                    param("k", key)
                }.await()
                if (first && data.data.datas.isEmpty()) {
                    //如果第一次切换且数据为空显示空缺省页
                    showEmpty()
                } else {
                    //设置初次创建页面为否
                    first = false
                    index += if (index == 0) { //下拉刷新
                        //设置数据
                        adapter.setList(data.data.datas)
                        //翻页
                        1
                    } else { //上拉加载更多
                        if (data.data.datas.isNullOrEmpty()) {
                            //没有更多数据，结束动画，显示内容(没有更多数据)
                            showContent(false)
                            return@scope
                        }
                        //添加数据
                        adapter.addData(data.data.datas)
                        //翻页
                        1
                    }
                    showContent(true)
                }
            }
        }.refreshing()
    }
}