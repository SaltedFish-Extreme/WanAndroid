package com.example.wanAndroid.logic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** 收藏数据类 */
@Parcelize
data class CollectResponse(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int,
) : Parcelable