package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.PageRefreshLayout
import com.drake.net.Get
import com.drake.net.utils.scope
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.model.CoinInfoResponse
import com.example.wanAndroid.logic.model.base.ApiPagerResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.LeaderboardAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.bar.TitleBar

/**
 * Created by 咸鱼至尊 on 2022/2/24
 *
 * desc: 积分排行页Activity
 */
class LeaderboardActivity : BaseActivity() {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val page: PageRefreshLayout by lazy { findViewById(R.id.page) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }
    private val integralMe: LinearLayout by lazy { findViewById(R.id.integral_me) }
    private val integralMyRank: TextView by lazy { findViewById(R.id.integral_myRank) }
    private val integralMyName: TextView by lazy { findViewById(R.id.integral_myName) }
    private val integralMyLv: TextView by lazy { findViewById(R.id.integral_myLv) }
    private val integralMyCount: TextView by lazy { findViewById(R.id.integral_myCount) }

    /** 是否初次切换页面 */
    private var first = true

    /** 数据集 */
    private lateinit var data: ApiResponse<ApiPagerResponse<ArrayList<CoinInfoResponse>>>

    /** 适配器 */
    private val adapter: LeaderboardAdapter by lazy { LeaderboardAdapter(arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        titleBar.leftView.setOnClickListener { finish() }
        //设置此页面请求分页初始索引
        PageRefreshLayout.startIndex = 1
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        if (AppConfig.UserName.isNotEmpty()) {
            integralMyRank.text = AppConfig.Rank
            integralMyName.text = AppConfig.UserName
            integralMyLv.text = getString(R.string.integral_my_lv, AppConfig.Level)
            integralMyCount.text = AppConfig.CoinCount
        } else {
            integralMe.visibility = View.GONE
        }
        //设置RecycleView的Adapter
        rv.adapter = adapter
        //刷新数据
        onRefresh()
    }

    /** 页面刷新加载操作，不设置onLoadMore则都会走onRefresh */
    private fun onRefresh() {
        page.onRefresh {
            scope {
                //获取积分排行数据
                data = Get<ApiResponse<ApiPagerResponse<ArrayList<CoinInfoResponse>>>>("${NetApi.LeaderboardAPI}/$index/json").await()
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

    override fun onDestroy() {
        rv.cancelFloatBtn(fab)
        super.onDestroy()
    }
}