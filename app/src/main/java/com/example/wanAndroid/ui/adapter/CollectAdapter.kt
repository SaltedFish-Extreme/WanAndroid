package com.example.wanAndroid.ui.adapter

import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.channel.receiveEvent
import com.drake.channel.sendEvent
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.CollectResponse
import com.example.wanAndroid.logic.model.NoDataResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.ext.html2String
import com.example.wanAndroid.widget.view.CollectView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.delay
import per.goweii.reveallayout.RevealLayout

/**
 * Created by 咸鱼至尊 on 2022/2/26
 *
 * desc: 收藏列表适配器
 */
class CollectAdapter(private val lifecycleOwner: LifecycleOwner) : BaseAdapter<CollectResponse>(R.layout.item_collect_list) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position ->
            //跳转文章网页打开链接，传递文章id标题链接及收藏与否，外加一个收藏文章原始id，同时将数据类传递过去
            data[position].run { WebActivity.start(context, id, title, link, true, originId = originId, data = this) }
        }
        //接收消息事件，同步收藏列表(从adapter移除对应数据)，默认自动在ON_DESTROY生命周期取消接收
        lifecycleOwner.receiveEvent<CollectResponse> { remove(it) }
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        viewHolder.getView<CollectView>(R.id.item_article_collect).setOnClickListener(object : CollectView.OnClickListener {
            //收藏控件点击事件回调
            override fun onClick(v: CollectView) {
                if (!v.isChecked) {
                    //收藏页面默认全部选中，点击后直接取消收藏，并移除item
                    lifecycleOwner.scopeNetLife {
                        Post<NoDataResponse>("${NetApi.UserUnCollectArticleAPI}/${data[viewHolder.bindingAdapterPosition].id}/json") {
                            param("originId", "${data[viewHolder.bindingAdapterPosition].originId}")
                        }.await()
                        //延迟一小会等取消收藏动画结束再删除item，增强一丢丢用户体验~
                        delay(300)
                        removeAt(viewHolder.bindingAdapterPosition)
                    }
                    //发送消息事件，取消收藏(将文章的id传递过去)
                    sendEvent(data[viewHolder.bindingAdapterPosition].originId, "tag_collect_cancel")
                }
            }
        })
    }

    override fun convert(holder: BaseViewHolder, item: CollectResponse) {
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
            holder.getView<RevealLayout>(R.id.item_article_collect).setChecked(true, false)
            //项目图片
            holder.setGone(R.id.item_article_image, envelopePic.isEmpty())
            if (envelopePic.isNotEmpty()) {
                //加载图片
                holder.getView<ShapeableImageView>(R.id.item_article_image).run {
                    Glide.with(context).load(envelopePic).placeholder(R.drawable.bg_project).transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(this)
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