package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Sting
import com.example.wanAndroid.widget.ext.randomColor

/**
 * Created by 咸鱼至尊 on 2022/1/31
 *
 * desc: 导航子流标签适配器
 */
class NavigationChildAdapter(dataList: MutableList<ArticleResponse>) : BaseAdapter<ArticleResponse>(R.layout.item_flow_shape, dataList) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position ->
            WebActivity.start(context, dataList[position].link)
        }
    }

    override fun convert(holder: BaseViewHolder, item: ArticleResponse) {
        //标签文字
        holder.setText(R.id.flow_tag, item.title.html2Sting())
        //标签文字颜色
        holder.setTextColor(R.id.flow_tag, randomColor())
    }
}