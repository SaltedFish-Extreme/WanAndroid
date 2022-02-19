package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.base.BaseAdapter

/**
 * Created by 咸鱼至尊 on 2022/2/17
 *
 * desc: 搜索历史适配器
 */
class SearchHistoryAdapter(dataList: MutableList<String>) : BaseAdapter<String>(R.layout.item_search_history_list, dataList) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_history_text, item)
    }
}