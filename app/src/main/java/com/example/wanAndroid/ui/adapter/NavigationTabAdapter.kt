package com.example.wanAndroid.ui.adapter

import androidx.core.content.ContextCompat
import com.example.wanAndroid.MyApplication.Companion.context
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.NavigationResponse
import q.rorbin.verticaltablayout.adapter.TabAdapter
import q.rorbin.verticaltablayout.widget.ITabView

/**
 * Created by 咸鱼至尊 on 2022/1/31
 *
 * desc: 自定义导航选项卡标签适配器
 */
class NavigationTabAdapter(private val dataList: List<NavigationResponse>) : TabAdapter {

    override fun getIcon(position: Int): ITabView.TabIcon? = null

    override fun getBadge(position: Int): ITabView.TabBadge? = null

    override fun getBackground(position: Int): Int = -1

    override fun getTitle(position: Int): ITabView.TabTitle {
        return ITabView.TabTitle.Builder()
            //标签文字
            .setContent(dataList[position].name)
            //标签文字选中及未选中颜色
            .setTextColor(ContextCompat.getColor(context, R.color.color_vertical_tab_layout_text), ContextCompat.getColor(context, R.color.gray_8f))
            .build()
    }

    override fun getCount(): Int = dataList.size

}