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
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.ArticleAdapter
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by 咸鱼至尊 on 2022/1/27
 *
 * desc: 子公众号Fragment
 */
class PlatformChildFragment : Fragment() {

    private val page: PageRefreshLayout by lazy { requireView().findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fab) }

    /** 是否初次切换页面 */
    private var first = true

    /** 该子公众号对应的id */
    private var cid = 0

    /** 适配器 */
    private val adapter: ArticleAdapter by lazy { ArticleAdapter(this) }

    /** 数据集 */
    private lateinit var data: ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>

    companion object {
        /**
         * 实例化fragment
         *
         * @param cid 项目id
         * @return fragment对象
         */
        fun newInstance(cid: Int): PlatformChildFragment {
            val args = Bundle()
            //通过Bundle传递构造参数
            args.putInt("cid", cid)
            val fragment = PlatformChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            //项目id
            cid = it.getInt("cid")
        }
    }

    override fun onResume() {
        super.onResume()
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 1
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
        rv.cancelFloatBtn(fab)
        super.onPause()
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取公众号历史数据
                data = Get<ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>>("${NetApi.PlatformDataAPI}/$cid/$index/json").await()
                if (first && data.data.datas.isEmpty()) {
                    //如果第一次切换且数据为空显示空缺省页
                    showEmpty()
                } else {
                    //设置初次创建页面为否
                    first = false
                    index += if (index == 1) { //下拉刷新
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