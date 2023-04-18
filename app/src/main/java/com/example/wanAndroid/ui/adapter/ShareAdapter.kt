package com.example.wanAndroid.ui.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.channel.receiveTag
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ArticleResponse
import com.example.wanAndroid.logic.model.NoDataResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.ui.activity.WebActivity
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.dialog.Dialog
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.view.CollectView
import com.hjq.toast.Toaster

/**
 * Created by 咸鱼至尊 on 2022/2/24
 *
 * desc: 分享列表适配器
 */
class ShareAdapter(private val lifecycleOwner: LifecycleOwner) : BaseAdapter<ArticleResponse>(R.layout.item_share_list) {

    companion object {
        //item的位置
        private var index: Int = 0
    }

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //先注册需要点击的子控件id
        addChildClickViewIds(R.id.share_item, R.id.share_delete)
        //设置子控件点击监听
        setOnItemChildClickListener { adapter, view, position ->
            //获取到对应的item数据
            val item = adapter.data[position] as ArticleResponse
            when (view.id) {
                R.id.share_item -> {
                    //跳转文章网页打开链接，传递文章id标题链接及收藏与否
                    item.run { WebActivity.start(context, id, title, link, collect) }
                    //跳转后将位置传递
                    index = position
                }
                R.id.share_delete -> {
                    //删除对应分享文章
                    Dialog.getConfirmDialog(context, context.getString(R.string.delete_share_confirm)) { _, _ ->
                        lifecycleOwner.scopeNetLife {
                            //从服务器删除对应文章
                            Post<NoDataResponse>("${NetApi.DeleteShareAPI}/${item.id}/json").await()
                        }
                        //adapter中删除
                        adapter.removeAt(position)
                        Toaster.show(context.getString(R.string.delete_succeed))
                    }.show()
                }
            }
        }
        //接收消息事件，同步收藏与否
        lifecycleOwner.receiveTag(true.toString(), false.toString()) {
            //将对应的数据类的收藏字段修改
            getItem(index).collect = it.toBoolean()
            //根据item的位置获取到它的收藏控件对象
            val collectView = getViewByPosition(index, R.id.item_share_collect) as CollectView
            //收藏控件是否选中
            collectView.isChecked = it.toBoolean()
        }
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        viewHolder.getView<CollectView>(R.id.item_share_collect).setOnClickListener(object : CollectView.OnClickListener {
            //收藏控件点击事件回调
            override fun onClick(v: CollectView) {
                if (v.isChecked) {
                    //选中收藏文章
                    lifecycleOwner.scopeNetLife {
                        Post<NoDataResponse>("${NetApi.CollectArticleAPI}/${data[viewHolder.bindingAdapterPosition - headerLayoutCount].id}/json").await()
                    }
                } else {
                    //未选中取消收藏文章
                    lifecycleOwner.scopeNetLife {
                        Post<NoDataResponse>("${NetApi.UnCollectArticleAPI}/${data[viewHolder.bindingAdapterPosition - headerLayoutCount].id}/json").await()
                    }
                }
                //收藏控件点击后，同步一下数据类，跳转网页同步收藏
                data[viewHolder.bindingAdapterPosition - headerLayoutCount].collect = v.isChecked
            }
        })
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
            holder.getView<CollectView>(R.id.item_share_collect).setChecked(collect, false)
            //是否上新
            holder.setGone(R.id.item_share_new, (!fresh))
            //是否审核
            holder.setGone(R.id.item_share_review, audit != 0)
        }
    }
}