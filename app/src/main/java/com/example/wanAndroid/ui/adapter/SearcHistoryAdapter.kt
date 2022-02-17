package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.base.BaseAdapter

class SearcHistoryAdapter(dataList: MutableList<String>) : BaseAdapter<String>(R.layout.item_history_list, dataList) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_history_text, item)
    }
}