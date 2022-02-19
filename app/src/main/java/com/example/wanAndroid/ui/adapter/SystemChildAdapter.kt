package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ClassificationResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Sting

/**
 * Created by 咸鱼至尊 on 2022/2/2
 *
 * desc: 体系子流标签适配器
 */
class SystemChildAdapter(dataList: MutableList<ClassificationResponse>) : BaseAdapter<ClassificationResponse>(R.layout.item_flow_text, dataList) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: ClassificationResponse) {
        //标签文字
        holder.setText(R.id.flow_tag, item.name.html2Sting())
    }
}