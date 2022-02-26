package com.example.wanAndroid.ui.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.logic.model.CollectArticleResponse
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.ext.html2String
import com.google.android.material.imageview.ShapeableImageView
import per.goweii.reveallayout.RevealLayout

/**
 * Created by 咸鱼至尊 on 2022/2/26
 *
 * desc: 收藏文章适配器
 */
class CollectArticleAdapter : BaseAdapter<CollectArticleResponse>(R.layout.item_collect_article_list) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position ->
            //跳转文章网页打开链接，传递文章id标题和链接
            data[position].run { WebActivity.start(context, id, title, link) }
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

    override fun convert(holder: BaseViewHolder, item: CollectArticleResponse) {
        item.run {
            //作者
            holder.setText(R.id.item_article_author, author.ifEmpty { context.getString(R.string.anonymous) })
            //发布日期
            holder.setText(R.id.item_article_date, niceDate)
            //文章标题
            holder.setText(R.id.item_article_title, title.html2Spanned())
            //文章章节
            holder.setText(R.id.item_article_chapter, (chapterName).html2Spanned())
            //全部收藏
            holder.getView<RevealLayout>(R.id.item_article_collect).isChecked = true
            //项目图片
            holder.setGone(R.id.item_article_image, envelopePic.isEmpty())
            if (envelopePic.isNotEmpty()) {
                //加载图片
                holder.getView<ShapeableImageView>(R.id.item_article_image).run {
                    Glide.with(context).load(envelopePic).placeholder(R.drawable.bg_project)
                        .transition(DrawableTransitionOptions.withCrossFade(500)).into(this)
                }
            }
            //项目内容
            holder.setGone(R.id.item_article_content, desc.isEmpty())
            if (desc.isNotEmpty()) {
                //内容描述
                holder.setText(R.id.item_article_content, desc.html2String())
            }
        }
    }
}