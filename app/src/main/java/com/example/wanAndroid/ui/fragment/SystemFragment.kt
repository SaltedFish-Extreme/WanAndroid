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
import com.example.wanAndroid.logic.model.SystemResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.SystemAdapter
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by 咸鱼至尊 on 2022/2/2
 *
 * desc: 子体系Fragment
 */
class SystemFragment : Fragment() {

    private val page: PageRefreshLayout by lazy { requireView().findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fab) }

    /** 是否初次切换页面 */
    private var first = true

    /** 适配器 */
    private val adapter: SystemAdapter by lazy { SystemAdapter(mutableListOf()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //第一次切换
        if (first) {
            //设置RecycleView的Adapter
            rv.adapter = adapter
            //刷新数据
            page.onRefresh {
                scope {
                    //获取体系数据
                    val data = Get<ApiResponse<ArrayList<SystemResponse>>>(NetApi.SystemAPI).await()
                    if (first && data.data.isEmpty()) {
                        //如果第一次切换且数据为空显示空缺省页
                        showEmpty()
                    } else {
                        //设置初次创建页面为否
                        first = false
                        //设置数据
                        adapter.setList(data.data)
                        //没有更多数据，结束动画，显示内容(没有更多数据)
                        showContent(false)
                    }
                }
            }.refreshing()
        }
    }

    override fun onPause() {
        super.onPause()
        rv.cancelFloatBtn(fab)
    }
}