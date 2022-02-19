package com.example.wanAndroid.ui.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.ui.activity.AuthorActivity
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.util.vibration
import com.example.wanAndroid.widget.ext.html2Sting
import com.google.android.material.imageview.ShapeableImageView
import com.hjq.toast.ToastUtils
import per.goweii.reveallayout.RevealLayout

/**
 * Created by 咸鱼至尊 on 2022/1/20
 *
 * desc: 文章布局适配器
 *
 * @param showTag 是否显示标签
 */
class ArticleAdapter(private val showTag: Boolean = false) : BaseAdapter<ArticleResponse>(R.layout.item_article_list) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position ->
            //跳转文章网页打开链接，传递文章id标题和链接
            data[position].run { WebActivity.start(context, id, title, link) }
        }
        //先注册需要点击的子控件id
        this.addChildClickViewIds(R.id.item_article_author, R.id.item_article_collect)
        //设置子控件点击监听
        this.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.item_article_author -> {
                    //打开文章作者页面
                    context.openActivity<AuthorActivity>(
                        //传递name和userId
                        "name" to data[position].run { author.ifEmpty { shareUser } },
                        "userId" to data[position].run { userId }
                    )
                }
                R.id.item_article_collect -> context.vibration() //震动一下
            }
        }
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        viewHolder.getView<RevealLayout>(R.id.item_article_collect).setOnCheckedChangeListener { _, isChecked ->
            //选中状态改变监听
            if (isChecked) ToastUtils.debugShow("喜欢！${data[viewHolder.layoutPosition].id}") else ToastUtils.debugShow("不喜欢！")
        }
    }

    override fun convert(holder: BaseViewHolder, item: ArticleResponse) {
        item.run {
            //作者or分享人
            holder.setText(R.id.item_article_author, author.ifEmpty { shareUser })
            //发布日期
            holder.setText(R.id.item_article_date, niceDate)
            //文章标题
            holder.setText(R.id.item_article_title, title.html2Sting())
            //文章章节
            holder.setText(R.id.item_article_chapter, ("$superChapterName·$chapterName").html2Sting())
            //是否收藏
            holder.getView<RevealLayout>(R.id.item_article_collect).isChecked = collect
            //是否置顶
            holder.setGone(R.id.item_article_top, (type != 1))
            //是否上新
            holder.setGone(R.id.item_article_new, (!fresh))
            //显示标签
            if (showTag) {
                //是否标签
                holder.setGone(R.id.item_article_tag, tags.isNullOrEmpty())
                if (tags.isNotEmpty()) {
                    //标签文字
                    holder.setText(R.id.item_article_tag, tags[0].name)
                }
            } else {
                //隐藏标签
                holder.setGone(R.id.item_article_tag, true)
            }
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
                holder.setText(R.id.item_article_content, desc.html2Sting())
            }
        }
    }
}