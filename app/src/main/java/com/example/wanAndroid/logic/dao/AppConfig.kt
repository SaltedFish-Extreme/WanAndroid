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
    var SearchHistory: ArrayList<String> by serialLazy(arrayListOf())

    /** 玩安卓登陆后返回的Cookie 永久保存磁盘，app删除或者赋值为null清除 */
    var Cookie: HashMap<String, String> by serialLazy(hashMapOf())

    /** 用户名 永久保存磁盘，app删除或者赋值为null清除 */
    var UserName: String by serialLazy("")

    /** 密码 永久保存磁盘，app删除或者赋值为null清除 */
    var PassWord: String by serialLazy("")

    /** 等级 永久保存磁盘，app删除或者赋值为null清除 */
    var Level: String by serialLazy("")

    /** 排名 永久保存磁盘，app删除或者赋值为null清除 */
    var Rank: String by serialLazy("")

    /** 积分 永久保存磁盘，app删除或者赋值为null清除 */
    var CoinCount: String by serialLazy("")
}