package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Sting
import com.example.wanAndroid.widget.ext.randomColor
import com.hjq.toast.ToastUtils

/**
 * Created by 咸鱼至尊 on 2022/1/31
 *
 * desc: 自定义导航子流标签适配器
 */
class NavigationChildAdapter(dataList: MutableList<ArticleResponse>) : BaseAdapter<ArticleResponse>(R.layout.item_flow_shape, dataList) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position -> ToastUtils.debugShow("我被点击了！ ${dataList[position].title}") }
    }

    override fun convert(holder: BaseViewHolder, item: ArticleResponse) {
        //标签文字
        holder.setText(R.id.flow_tag, item.title.html2Sting())
        //标签文字颜色
        holder.setTextColor(R.id.flow_tag, randomColor())
    }

}