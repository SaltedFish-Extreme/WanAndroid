package com.example.wanAndroid.logic.dao

import com.drake.serialize.serialize.serialLazy
import com.example.wanAndroid.logic.model.SearchHotResponse

/**
 * Created by 咸鱼至尊 on 2022/2/7
 *
 * desc: 应用配置的本地数据类
 */
object AppConfig {
    /** 是否夜间主题 */
    var DarkTheme: Boolean by serialLazy(false) //懒加载

    /** 搜索热词 每次打开app重新初始化赋值一次 */
    val SearchHot: MutableList<SearchHotResponse> by lazy { (mutableListOf()) }

    /** 搜索记录 永久保存磁盘，app删除或者赋值为null清除 */
    var SearchHistory: ArrayList<String>? by serialLazy()
}