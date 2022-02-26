package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.logic.model.CollectURLResponse
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import per.goweii.reveallayout.RevealLayout

/**
 * Created by 咸鱼至尊 on 2022/2/26
 *
 * desc: 收藏网址适配器
 */
class CollectURLAdapter : BaseAdapter<CollectURLResponse>(R.layout.item_collect_url_list) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position ->
            //跳转网页打开链接
            data[position].run { WebActivity.start(context, link) }
        }
        //先注册需要点击的子控件id
        this.addChildClickViewIds(R.id.item_article_collect)
        //设置子控件点击监听
        this.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.item_article_collect -> context.vibration() //震动一下
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: CollectURLResponse) {
        //标题
        holder.setText(R.id.item_url_title, item.name)
        //链接
        holder.setText(R.id.item_url_link, item.link)
        //收藏
        holder.getView<RevealLayout>(R.id.item_url_collect).isChecked = true
    }
}