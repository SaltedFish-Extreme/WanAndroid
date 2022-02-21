package com.example.wanAndroid.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.NavigationResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Spanned
import com.google.android.flexbox.FlexboxLayoutManager

/**
 * Created by 咸鱼至尊 on 2022/1/31
 *
 * desc: 导航内容适配器
 */
class NavigationContentAdapter(dataList: MutableList<NavigationResponse>) : BaseAdapter<NavigationResponse>(R.layout.item_navigation_list, dataList) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        //获取rv
        viewHolder.getView<RecyclerView>(R.id.rv).run {
            //使用伸缩布局
            layoutManager = FlexboxLayoutManager(context)
            //避免item改变重新绘制rv
            setHasFixedSize(true)
            //禁用嵌套滚动
            isNestedScrollingEnabled = false
        }
    }

    override fun convert(holder: BaseViewHolder, item: NavigationResponse) {
        //设置rv标题
        holder.setText(R.id.title, item.name.html2Spanned())
        //使用子流标签适配器
        holder.getView<RecyclerView>(R.id.rv).adapter = NavigationChildAdapter(item.articles)
    }
}