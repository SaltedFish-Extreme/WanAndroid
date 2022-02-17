package com.example.wanAndroid.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.model.SearchHotResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.SearcHistoryAdapter
import com.example.wanAndroid.ui.adapter.SearcHotAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.view.ClearEditText
import com.example.wanAndroid.widget.view.PressAlphaTextView
import com.google.android.flexbox.FlexboxLayoutManager
import com.hjq.toast.ToastUtils

/**
 * Created by 咸鱼至尊 on 2022/2/16
 *
 * desc: 搜索页Activity
 */
class SearchActivity : BaseActivity() {

    private val back: ImageView by lazy { findViewById(R.id.back) }
    private val searchText: ClearEditText by lazy { findViewById(R.id.search_text) }
    private val search: ImageView by lazy { findViewById(R.id.search) }
    private val rvHot: RecyclerView by lazy { findViewById(R.id.rv_hot) }
    private val clear: PressAlphaTextView by lazy { findViewById(R.id.clear) }
    private val rvHistory: RecyclerView by lazy { findViewById(R.id.rv_history) }

    /** 搜索热词适配器 */
    private val hotAdapter: SearcHotAdapter by lazy { SearcHotAdapter(arrayListOf()) }

    /** 搜索历史适配器 */
    private val historyAdapter: SearcHistoryAdapter by lazy { SearcHistoryAdapter(arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        //返回图标关闭页面
        back.setOnClickListener { finish() }
        //初始化搜索热词
        initSearchHot()
        //初始化搜索历史
        initSearchHistory()
        //清空图标重置存储的历史记录并重置adapter
        clear.setOnClickListener {
            AppConfig.SearchHistory = arrayListOf()
            historyAdapter.setList(arrayListOf())
        }
        //搜索图标跳转搜索结果Activity并更新搜索记录
        search.setOnClickListener {
            //文本为空不执行操作
            if (searchText.text.isNullOrBlank()) {
                return@setOnClickListener
            } else {
                ToastUtils.debugShow(searchText.text)
                updateKey(searchText.text.toString())
            }
        }
    }

    /** 初始化搜索热词 */
    @SuppressLint("NotifyDataSetChanged")
    private fun initSearchHot() {
        if (AppConfig.SearchHot.isNullOrEmpty()) {
            //没有存储过搜索热词就发起请求
            scopeNetLife {
                //请求搜索热词数据
                val data = Get<ApiResponse<MutableList<SearchHotResponse>>>(NetApi.SearchHotAPI).await()
                //存储搜索热词数据
                AppConfig.SearchHot.addAll(data.data)
                //给adapter设置数据
                hotAdapter.setList(data.data)
                //刷新adapter数据
                hotAdapter.notifyDataSetChanged()
            }
        } else {
            //存储过搜索热词直接获取并设置给adapter
            hotAdapter.setList(AppConfig.SearchHot)
        }
        //初始化热门搜索RecyclerView
        rvHot.run {
            //使用伸缩布局
            layoutManager = FlexboxLayoutManager(context)
            //避免item改变重新绘制rv
            setHasFixedSize(true)
            //禁用嵌套滚动
            isNestedScrollingEnabled = false
            //设置adapter
            rvHot.adapter = hotAdapter.run {
                //点击热词item标签跳转搜索结果Activity并更新搜索记录
                setOnItemClickListener { _, _, position ->
                    ToastUtils.debugShow(this.data[position].name)
                    updateKey(this.data[position].name)
                }
                //返回此adapter
                this
            }
        }
    }

    /** 初始化搜索历史 */
    private fun initSearchHistory() {
        if (AppConfig.SearchHistory.isNullOrEmpty()) {
            //没有搜索记录则初始化
            AppConfig.SearchHistory = arrayListOf()
        } else {
            //搜索过则给adapter设置数据
            historyAdapter.setList(AppConfig.SearchHistory)
        }
        //设置adapter
        rvHistory.adapter = historyAdapter.run {
            //点击历史记录item跳转搜索结果Activity并更新搜索记录
            setOnItemClickListener { _, _, position ->
                ToastUtils.debugShow(this.data[position])
                updateKey(this.data[position])
            }
            //注册需要点击的子控件id
            this.addChildClickViewIds(R.id.item_history_image)
            this.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    //点击子item删除图标
                    R.id.item_history_image -> {
                        //从adapter删除指定数据
                        historyAdapter.removeAt(position)
                        AppConfig.SearchHistory?.run {
                            //从存储中删除指定数据
                            removeAt(position)
                            //改变序列化对象内的字段要求重新赋值
                            AppConfig.SearchHistory = this
                        }
                    }
                }
            }
            //返回此adapter
            this
        }
    }

    /** 更新搜索记录 */
    private fun updateKey(keyStr: String) {
        AppConfig.SearchHistory?.let {
            if (it.contains(keyStr)) {
                //当搜索记录中包含该数据时 删除
                it.remove(keyStr)
                //同时从adapter中删除
                historyAdapter.remove(keyStr)
            } else if (it.size >= 10) {
                //如果集合的size 有10个以上了，删除最后一个
                it.removeAt(it.size - 1)
                //同时从adapter中删除
                historyAdapter.removeAt(it.size - 1)
            }
            //添加新数据到第一条
            it.add(0, keyStr)
            //同时添加到adapter
            historyAdapter.addData(0, keyStr)
            //改变序列化对象内的字段要求重新赋值
            AppConfig.SearchHistory = it
            //滚动到rv顶部
            rvHistory.scrollToPosition(0)
        }
    }
}