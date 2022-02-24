package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.CoinInfoResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.randomColor

/**
 * Created by 咸鱼至尊 on 2022/2/23
 *
 * desc: 积分排行适配器
 */
class LeaderboardAdapter(dataList: MutableList<CoinInfoResponse>) : BaseAdapter<CoinInfoResponse>(R.layout.item_leaderboard_list, dataList) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: CoinInfoResponse) {
        holder.setText(R.id.item_integral_rank, item.rank)
            .setText(R.id.item_integral_lv, context.getString(R.string.integral_lv, item.level))
            .setTextColor(R.id.item_integral_lv, randomColor())
            .setText(R.id.item_integral_name, item.username)
            .setText(R.id.item_integral_count, item.coinCount.toString())
    }
}