package com.example.wanAndroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.logic.model.BannerResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.ArticleAdapter
import com.example.wanAndroid.ui.adapter.ImageTitleAdapter
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.example.wanAndroid.widget.toolbar.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 首页Fragment
 */
class HomeFragment : Fragment() {

    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rv) }
    private val page: PageRefreshLayout by lazy { requireView().findViewById(R.id.page) }
    private val fab: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fab) }
    private val toolbar: Toolbar by lazy { requireActivity().findViewById(R.id.toolbar) }
    private val adapter: ArticleAdapter by lazy { ArticleAdapter(true) }

    /** 是否初次切换页面 */
    private var first = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //设置RecycleView的Adapter
        rv.adapter = adapter
        //刷新
        onRefresh()
        //加载
        onLoadMore()
    }

    /** 页面刷新操作，刷新回调 */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                if (first) {
                    //初次创建页面，获取首页轮播图数据
                    val mBannerData = Get<ApiResponse<ArrayList<BannerResponse>>>(NetApi.BannerAPI).await()
                    if (mBannerData.data.isEmpty()) {
                        //如果数据为空显示空缺省页
                        showEmpty()
                        return@scope
                    }
                    //填充头部视图
                    val mHeaderView =
                        LayoutInflater.from(context).inflate(R.layout.layout_banner, requireActivity().window.decorView as ViewGroup, false).apply {
                            findViewById<Banner<BannerResponse, ImageTitleAdapter>>(R.id.banner).apply {
                                //使用自定义的banner适配器并设置数据
                                setAdapter(ImageTitleAdapter(mBannerData.data))
                                //设置圆点指示器
                                indicator = CircleIndicator(context)
                                //添加生命周期管理
                                addBannerLifecycleObserver(this@HomeFragment)
                            }
                        }
                    //给adapter设置头部视图
                    adapter.addHeaderView(mHeaderView)
                    //设置初次创建页面为否
                    first = false
                }
                //置顶文章请求
                val mArticleTopDeferred =
                    Get<ApiResponse<ArrayList<ArticleResponse>>>(NetApi.ArticleTopAPI)
                //文章列表请求
                val mArticleListDeferred =
                    Get<ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>>("${NetApi.ArticleListAPI}/$index/json")
                val mArticleTopData = mArticleTopDeferred.await() //获取置顶文章数据
                val mArticleListData = mArticleListDeferred.await() //获取文章列表数据
                //给adapter设置数据
                adapter.setList(mArticleTopData.data + mArticleListData.data.datas)
                //翻页
                index += 1
                //结束动画，显示内容(有更多数据)
                showContent(true)
            }
            //自动刷新及缺省页
        }.refreshing()
    }

    /** 页面加载操作，加载回调 */
    private fun onLoadMore() {
        page.onLoadMore {
            scope {
                //获取文章列表数据
                val mArticleListData =
                    Get<ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>>("${NetApi.ArticleListAPI}/$index/json").await()
                if (mArticleListData.data.datas.isNullOrEmpty()) {
                    //没有更多数据，结束动画，显示内容(没有更多数据)
                    showContent(false)
                    return@scope
                }
                //给adapter添加数据
                adapter.addData(mArticleListData.data.datas)
                //翻页
                index += 1
                //结束动画，显示内容(有更多数据)
                showContent(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //给toolbar设置标题
        toolbar.title = getString(R.string.home_fragment)
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 0
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
    }

    override fun onPause() {
        rv.cancelFloatBtn(fab)
        super.onPause()
    }
}