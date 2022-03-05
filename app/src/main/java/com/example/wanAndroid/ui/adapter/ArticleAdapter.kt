package com.example.wanAndroid.ui.adapter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.channel.receiveTag
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.logic.model.NoDataResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.activity.AuthorActivity
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.ext.html2String
import com.example.wanAndroid.widget.view.CollectView
import com.google.android.material.imageview.ShapeableImageView

/**
 * Created by 咸鱼至尊 on 2022/1/20
 *
 * desc: 文章布局适配器
 *
 * @param showTag 是否显示标签
 */
class ArticleAdapter(private val lifecycleOwner: LifecycleOwner, private val showTag: Boolean = false) :
    BaseAdapter<ArticleResponse>(R.layout.item_article_list) {

    companion object {
        //item的位置
        private var index: Int = 0
    }

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position ->
            //跳转文章网页打开链接，传递文章id标题链接及收藏与否
            data[position].run { WebActivity.start(context, id, title, link, collect) }
            //跳转后将位置传递
            index = position
        }
        //先注册需要点击的子控件id
        this.addChildClickViewIds(R.id.item_article_author)
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
            }
        }
        //添加生命周期管理者
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver())
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        viewHolder.getView<CollectView>(R.id.item_article_collect).setOnClickListener(object : CollectView.OnClickListener {
            //收藏控件点击事件回调
            override fun onClick(v: CollectView) {
                if (v.isChecked) {
                    //选中收藏文章
                    lifecycleOwner.scopeNetLife {
                        Post<NoDataResponse>("${NetApi.CollectArticleAPI}/${data[viewHolder.adapterPosition - headerLayoutCount].id}/json").await()
                    }
                } else {
                    //未选中取消收藏文章
                    lifecycleOwner.scopeNetLife {
                        Post<NoDataResponse>("${NetApi.UnCollectArticleAPI}/${data[viewHolder.adapterPosition - headerLayoutCount].id}/json").await()
                    }
                }
            }
        })
    }

    override fun convert(holder: BaseViewHolder, item: ArticleResponse) {
        item.run {
            //作者or分享人
            holder.setText(R.id.item_article_author, author.ifEmpty { shareUser })
            //发布日期
            holder.setText(R.id.item_article_date, niceDate)
            //文章标题
            holder.setText(R.id.item_article_title, title.html2Spanned())
            //文章章节
            holder.setText(R.id.item_article_chapter, ("$superChapterName·$chapterName").html2Spanned())
            //是否收藏
            holder.getView<CollectView>(R.id.item_article_collect).setChecked(collect, false)
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
                holder.setText(R.id.item_article_content, desc.html2String())
            }
        }
    }

    /** 生命周期管理器 */
    private fun lifecycleObserver(): LifecycleEventObserver {
        return LifecycleEventObserver { _, event ->
            //页面恢复时接收事件(复用adapter导致多个已打开页面重复接收消息)
            if (event == Lifecycle.Event.ON_RESUME) {
                //接收消息事件，页面暂停时注销
                lifecycleOwner.receiveTag(true.toString(), false.toString(), lifeEvent = Lifecycle.Event.ON_PAUSE) {
                    //根据打开网页的item的位置获取到它的收藏控件对象
                    val collectView = getViewByPosition(index + headerLayoutCount, R.id.item_article_collect) as CollectView
                    //收藏控件是否选中
                    collectView.isChecked = it.toBoolean()
                    //同时将对应的数据类的收藏字段修改
                    data[index].collect = it.toBoolean()
                }
            }
        }
    }
}