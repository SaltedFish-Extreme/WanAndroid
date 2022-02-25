package com.example.wanAndroid.logic.model

/** 文章数据类 */
data class ArticleResponse(
    val audit: Int,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Int,
    val userId: Int,
    val link: String,
    val niceDate: String,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<Tag>,
    val title: String,
    val type: Int,
) {
    data class Tag(
        val name: String,
        val url: String
    )
}