package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.IntegralListResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Spanned

/**
 * Created by 咸鱼至尊 on 2022/2/23
 *
 * desc: 积分获取适配器
 */
class IntegralAdapter(dataList: MutableList<IntegralListResponse>) : BaseAdapter<IntegralListResponse>(R.layout.item_integral_list, dataList) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: IntegralListResponse) {
        holder.setText(R.id.tv_reason, item.reason)
            .setText(R.id.tv_desc, item.desc.html2Spanned())
            .setText(R.id.tv_score, "+${item.coinCount}")
    }
}