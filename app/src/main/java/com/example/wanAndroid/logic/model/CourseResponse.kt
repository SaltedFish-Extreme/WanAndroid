package com.example.wanAndroid.logic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** 教程数据类 */
@Parcelize
data class CourseResponse(
    val articleList: List<String>,
    val author: String,
    val children: List<String>,
    val courseId: Int,
    val cover: String,
    val desc: String,
    val id: Int,
    val lisense: String,
    val lisenseLink: String,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val type: Int,
    val userControlSetTop: Boolean,
    val visible: Int
) : Parcelable