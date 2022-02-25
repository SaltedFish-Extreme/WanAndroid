package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Spanned
import per.goweii.reveallayout.RevealLayout

/**
 * Created by 咸鱼至尊 on 2022/1/20
 *
 * desc: 分享列表适配器
 */
class ShareAdapter : BaseAdapter<ArticleResponse>(R.layout.item_share_list) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //先注册需要点击的子控件id
        this.addChildClickViewIds(R.id.share_item, R.id.item_share_collect)
        //设置子控件点击监听
        this.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.share_item -> {
                    //跳转文章网页打开链接，传递文章id标题和链接
                    data[position].run { WebActivity.start(context, id, title, link) }
                }
                R.id.item_share_collect -> context.vibration() //震动一下
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: ArticleResponse) {
        item.run {
            //作者or分享人
            holder.setText(R.id.item_share_author, author.ifEmpty { shareUser })
            //发布日期
            holder.setText(R.id.item_share_date, niceDate)
            //文章标题
            holder.setText(R.id.item_share_title, title.html2Spanned())
            //文章章节
            holder.setText(R.id.item_share_chapter, ("$superChapterName·$chapterName").html2Spanned())
            //是否收藏
            holder.getView<RevealLayout>(R.id.item_share_collect).isChecked = collect
            //是否上新
            holder.setGone(R.id.item_share_new, (!fresh))
            //是否审核
            holder.setGone(R.id.item_share_review, audit != 0)
        }
    }
}