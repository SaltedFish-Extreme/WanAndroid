package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.SearchHotResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.randomColor

/**
 * Created by 咸鱼至尊 on 2022/2/16
 *
 * desc: 搜索热词适配器
 */
class SearchHotAdapter(dataList: MutableList<SearchHotResponse>) : BaseAdapter<SearchHotResponse>(R.layout.item_flow_shape, dataList) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: SearchHotResponse) {
        holder.setText(R.id.flow_tag, item.name)
        holder.setTextColor(R.id.flow_tag, randomColor())
    }
}