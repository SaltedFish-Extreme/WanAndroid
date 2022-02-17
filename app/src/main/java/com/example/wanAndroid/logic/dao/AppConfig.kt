package com.example.wanAndroid.logic.dao

import com.drake.serialize.serialize.serial
import com.example.wanAndroid.logic.model.SearchHotResponse

/**
 * Created by 咸鱼至尊 on 2022/2/7
 *
 * desc: 应用配置的本地数据类
 */
object AppConfig {
    /** 是否夜间主题 */
    var DarkTheme: Boolean by serial(false) //懒加载

    /** 搜索热词 */
    val SearchHot: MutableList<SearchHotResponse> by serial(mutableListOf())

    /** 搜索记录 */
    val SearchHistory: Set<String> by serial(mutableSetOf())
}