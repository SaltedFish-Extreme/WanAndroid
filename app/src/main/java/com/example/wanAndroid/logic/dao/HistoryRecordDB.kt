package com.example.wanAndroid.logic.dao

import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemSwipe
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * Created by 咸鱼至尊 on 2022/2/18
 *
 * desc: 历史记录数据库 继承数据库支持以及BRV侧滑
 *
 * @property itemOrientationSwipe 侧滑方向
 */
class HistoryRecordDB(override var itemOrientationSwipe: Int = ItemOrientation.HORIZONTAL) : LitePalSupport(), ItemSwipe {
    //页面标题
    @Column(unique = true, defaultValue = "未知")
    internal lateinit var title: String

    //页面链接
    @Column(nullable = false)
    internal lateinit var url: String

    //访问时间
    @Column(index = true)
    internal lateinit var date: Date
}