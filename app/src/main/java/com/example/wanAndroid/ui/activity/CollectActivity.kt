package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.CollectResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.CollectAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.bar.TitleBar

/**
 * Created by 咸鱼至尊 on 2022/2/26
 *
 * desc: 我的收藏页Activity
 */
class CollectActivity : BaseActivity() {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val page: PageRefreshLayout by lazy { findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    /** 是否初次切换页面 */
    private var first = true

    /** 适配器 */
    private val adapter: CollectAdapter by lazy { CollectAdapter() }

    /** 数据集 */
    private lateinit var data: ApiResponse<ApiPagerResponse<ArrayList<CollectResponse>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        titleBar.leftView.setOnClickListener { finish() }
        titleBar.title = getString(R.string.my_collect)
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 0
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //设置RecycleView的Adapter
        rv.adapter = adapter
        //刷新数据
        onRefresh()
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取收藏文章列表数据
                data = Get<ApiResponse<ApiPagerResponse<ArrayList<CollectResponse>>>>("${NetApi.CollectListAPI}/$index/json").await()
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

    override fun onDestroy() {
        rv.cancelFloatBtn(fab)
        super.onDestroy()
    }
}