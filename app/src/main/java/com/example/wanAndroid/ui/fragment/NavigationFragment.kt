package com.example.wanAndroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.NavigationResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.NavigationContentAdapter
import com.example.wanAndroid.ui.adapter.NavigationTabAdapter
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.widget.TabView

/**
 * Created by 咸鱼至尊 on 2022/1/30
 *
 * desc: 子导航Fragment
 */
class NavigationFragment : Fragment() {

    private val verticalTabLayout: VerticalTabLayout by lazy { requireView().findViewById(R.id.vertical_tab_layout) }
    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fab) }

    /** 线性布局管理器 */
    private val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(activity) }

    /** 是否初次切换页面 */
    private var first = true

    /** 是否选中标签 */
    private var clickTab = false

    /** 当前位置索引 */
    private var currentIndex = 0

    /** 是否可以滚动 */
    private var scroll = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onResume() {
        super.onResume()
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        if (first) {
            scopeNetLife {
                //获取导航数据
                val mNavigationData = Get<ApiResponse<ArrayList<NavigationResponse>>>(NetApi.NavigationAPI).await()
                //给左边标签设置适配器及数据
                verticalTabLayout.setTabAdapter(NavigationTabAdapter(mNavigationData.data))
                //给rv设置布局管理器
                rv.layoutManager = linearLayoutManager
                //给右边rv设置适配器及数据
                rv.adapter = NavigationContentAdapter(mNavigationData.data)
                //设置初次创建页面为否
                first = false
                //联动标签及rv
                linkLeftRight()
            }
        }
    }

    override fun onPause() {
        //这里不使用取消悬浮按钮扩展函数(删除rv滚动监听器会将TabLayout联动效果删除)
        rv.stopScroll()
        fab.hide()
        super.onPause()
    }

    /** 联动左边标签和右边rv */
    private fun linkLeftRight() {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (scroll && (newState == RecyclerView.SCROLL_STATE_IDLE)) {
                    scrollRecyclerView()
                }
                rightLinkLeft(newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (scroll) {
                    scrollRecyclerView()
                }
            }
        })

        verticalTabLayout.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabView?, position: Int) {}

            override fun onTabSelected(tab: TabView?, position: Int) {
                clickTab = true
                selectTab(position)
            }
        })
    }

    /** 右边rv联动左边标签 */
    private fun rightLinkLeft(newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (clickTab) {
                clickTab = false
                return
            }
            val firstPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
            if (firstPosition != currentIndex) {
                currentIndex = firstPosition
                setChecked(currentIndex)
            }
        }
    }

    /** 滚动rv */
    private fun scrollRecyclerView() {
        scroll = false
        val indexDistance: Int = currentIndex - linearLayoutManager.findFirstVisibleItemPosition()
        if (indexDistance > 0 && indexDistance < rv.childCount) {
            val top: Int = rv.getChildAt(indexDistance).top
            rv.smoothScrollBy(0, top)
        }
    }

    /** 滚动右边rv，以选择左边标签 */
    private fun setChecked(position: Int) {
        if (clickTab) {
            clickTab = false
        } else {
            verticalTabLayout.setTabSelected(currentIndex)
        }
        currentIndex = position
    }

    /** 选择左边标签，以滚动右边rv */
    private fun selectTab(position: Int) {
        currentIndex = position
        rv.stopScroll()
        smoothScrollToPosition(position)
    }

    /** rv平滑滚动到指定位置 */
    private fun smoothScrollToPosition(position: Int) {
        val firstPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition: Int = linearLayoutManager.findLastVisibleItemPosition()
        when {
            position <= firstPosition -> {
                rv.smoothScrollToPosition(position)
            }
            position <= lastPosition -> {
                val top: Int = rv.getChildAt(position - firstPosition).top
                rv.smoothScrollBy(0, top)
            }
            else -> {
                rv.smoothScrollToPosition(position)
                scroll = true
            }
        }
    }
}