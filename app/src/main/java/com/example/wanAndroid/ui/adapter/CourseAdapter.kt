package com.example.wanAndroid.ui.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.CourseResponse
import com.example.wanAndroid.ui.activity.CourseArticleActivity
import com.example.wanAndroid.ui.base.BaseAdapter

/**
 * Created by 咸鱼至尊 on 2022/11/17
 *
 * desc: 教程列表适配器
 */
class CourseAdapter : BaseAdapter<CourseResponse>(R.layout.item_course_list) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
        setOnItemClickListener { _, _, position ->
            //点击课程item，跳转对应的课程目录页，并传递参数
            context.openActivity<CourseArticleActivity>("courseData" to data[position])
        }
    }

    override fun convert(holder: BaseViewHolder, item: CourseResponse) {
        //课程图片
        holder.getView<ImageView>(R.id.item_course_image).run {
            if (item.cover.isNotEmpty()) {
                Glide.with(context).load(item.cover).placeholder(R.drawable.bg_course_article)
                    .transition(DrawableTransitionOptions.withCrossFade(500)).into(this)
            } else {
                Glide.with(context).load(R.drawable.bg_course_article).into(this)
            }
        }
        //课程标题
        holder.setText(R.id.item_course_title, item.name)
        //课程作者
        holder.setText(R.id.item_course_author, context.getString(R.string.course_author, item.author))
        //课程介绍
        holder.setText(R.id.item_course_desc, item.desc)
    }
}