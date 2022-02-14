package com.example.wanAndroid.ui.fragment

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.ArticleAdapter
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by 咸鱼至尊 on 2022/1/30
 *
 * desc: 子广场Fragment
 */
class SquareChildFragment : Fragment(R.layout.fragment_home) {

    private val page: PageRefreshLayout by lazy { requireView().findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fab) }

    /** 是否初次切换页面 */
    private var first = true

    /** 适配器 */
    private val adapter: ArticleAdapter by lazy { ArticleAdapter() }

    /** 数据集 */
    private lateinit var data: ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>

    override fun onResume() {
        super.onResume()
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 0
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //第一次切换
        if (first) {
            //设置RecycleView的Adapter
            rv.adapter = adapter
            //刷新数据
            onRefresh()
        }
    }

    override fun onPause() {
        super.onPause()
        rv.cancelFloatBtn(fab)
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取广场列表数据
                data = Get<ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>>("${NetApi.SquareListAPI}/$index/json").await()
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