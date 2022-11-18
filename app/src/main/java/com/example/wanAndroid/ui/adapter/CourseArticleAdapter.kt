package com.example.wanAndroid.ui.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.CourseArticleResponse
import com.example.wanAndroid.ui.base.BaseAdapter

/**
 * Created by 咸鱼至尊 on 2022/11/18
 *
 * desc: 课程目录列表适配器
 */
class CourseArticleAdapter : BaseAdapter<CourseArticleResponse>(R.layout.item_course_article_list) {

    init {
        setAnimationWithDefault(AnimationType.ScaleIn)
    }

    override fun convert(holder: BaseViewHolder, item: CourseArticleResponse) {
        //设置目录item序号
        holder.setText(R.id.item_course_article_num, "${holder.layoutPosition + 1}")
        //设置目录item名称
        holder.setText(R.id.item_course_article_name, item.title)
    }
}