package com.example.wanAndroid.logic.net

/**
 * Created by 咸鱼至尊 on 2022/1/8
 *
 * desc: 网络请求API类
 */
object NetApi {
    /** 全局根路径 */
    const val BaseURL = "https://www.wanandroid.com/"

    /** 根据昵称生成头像API根路径 */
    const val GenerateAvatarAPI = "https://api.multiavatar.com"

    /** 首页轮播图路径 */
    const val BannerAPI = "banner/json"

    /** 首页置顶文章路径 */
    const val ArticleTopAPI = "article/top/json"

    /** 首页文章列表路径 */
    const val ArticleListAPI = "article/list"

    /** 项目分类路径 */
    const val ProjectClassifyAPI = "project/tree/json"

    /** 最新项目路径 */
    const val ProjectNewAPI = "article/listproject"

    /** 项目列表路径 */
    const val ProjectListAPI = "project/list"

    /** 公众号列表路径 */
    const val PlatformListAPI = "wxarticle/chapters/json"

    /** 公众号历史数据路径 */
    const val PlatformDataAPI = "wxarticle/list"

    /** 广场列表数据路径 */
    const val SquareListAPI = "user_article/list"

    /** 问答数据路径 */
    const val InquiryAnswerAPI = "wenda/list"

    /** 体系数据路径 */
    const val SystemAPI = "tree/json"

    /** 导航数据路径 */
    const val NavigationAPI = "navi/json"

    /** 按照作者昵称搜索文章路径 */
    const val SearchArticleByNameAPI = "article/list"

    /** 分享人对应列表数据路径 */
    const val SearchArticleByIdAPI = "user"

    /** 知识体系下的文章路径 */
    const val SystemArticleAPI = "article/list"

    /** 搜索热词路径 */
    const val SearchHotAPI = "hotkey/json"

    /** 搜索结果路径 */
    const val SearchResultAPI = "article/query"

    /** 用户登陆路径 */
    const val LoginAPI = "user/login"

    /** 用户积分路径 */
    const val CoinInfoAPI = "lg/coin/userinfo/json"

    /** 用户退出路径 */
    const val ExitAPI = "user/logout/json"

    /** 用户注册路径 */
    const val RegisterAPI = "user/register"

    /** 积分获取列表路径 */
    const val IntegralListAPI = "lg/coin/list"

    /** 积分排行路径 */
    const val LeaderboardAPI = "coin/rank"

    /** 个人分享文章列表路径 */
    const val ShareListAPI = "user/lg/private_articles"

    /** 删除分享文章路径 */
    const val DeleteShareAPI = "lg/user_article/delete"

    /** 分享文章路径 */
    const val ShareArticleAPI = "lg/user_article/add/json"

    /** 收藏列表路径 */
    const val CollectListAPI = "lg/collect/list"

    /** 收藏文章路径 */
    const val CollectArticleAPI = "lg/collect"

    /** 取消收藏文章路径 */
    const val UnCollectArticleAPI = "lg/uncollect_originId"

    /** 收藏页面取消收藏文章路径 */
    const val UserUnCollectArticleAPI = "lg/uncollect"

    /** 教程列表路径 */
    const val CourseListAPI = "chapter/547/sublist/json"
}