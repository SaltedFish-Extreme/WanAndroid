package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.SearchHotResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.adapter.SearcHotAdapter
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.view.ClearEditText
import com.google.android.flexbox.FlexboxLayoutManager

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
    private val clear: TextView by lazy { findViewById(R.id.clear) }
    private val rvHistory: RecyclerView by lazy { findViewById(R.id.rv_history) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        back.setOnClickListener { finish() }
        scopeNetLife {
            scopeNetLife {
                //请求搜索热词数据
                val data = Get<ApiResponse<MutableList<SearchHotResponse>>>(NetApi.SearchHotAPI).await()
                //初始化热门rv
                rvHot.run {
                    //使用伸缩布局
                    layoutManager = FlexboxLayoutManager(context)
                    //避免item改变重新绘制rv
                    setHasFixedSize(true)
                    //禁用嵌套滚动
                    isNestedScrollingEnabled = false
                    //设置adapter
                    rvHot.adapter = SearcHotAdapter(data.data)
                }
            }
        }
    }
}