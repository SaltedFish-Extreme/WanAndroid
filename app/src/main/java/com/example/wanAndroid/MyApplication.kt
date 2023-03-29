package com.example.wanAndroid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.drake.brv.PageRefreshLayout
import com.drake.net.NetConfig
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.statelayout.StateConfig
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.logic.net.GsonConvert
import com.example.wanAndroid.logic.net.NetApi.BaseURL
import com.example.wanAndroid.util.MyCookieJar
import com.example.wanAndroid.widget.refresh.MyClassicsFooter
import com.example.wanAndroid.widget.refresh.MyClassicsHeader
import com.hjq.toast.Toaster
import com.jinrishici.sdk.android.factory.JinrishiciFactory
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import org.litepal.LitePal
import per.goweii.swipeback.SwipeBack
import per.goweii.swipeback.SwipeBackDirection
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer

/**
 * Created by 咸鱼至尊 on 2021/12/9
 *
 * desc: 全局Application对象
 */
class MyApplication : Application() {
    companion object {
        /** 全局context对象 */
        @SuppressLint("StaticFieldLeak")
        internal lateinit var context: Context

        /** 根据昵称生成头像API密钥 (https://api.multiavatar.com) */
        const val apikey = "a6YniOPf1dbrSc"
    }

    override fun onCreate() {
        super.onCreate()
        //延迟初始化全局context对象
        context = applicationContext
        //本地异常捕捉
        CrashHandler.register(this)
        //应用主题切换
        when (AppConfig.DarkTheme) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //初始化数据库
        LitePal.initialize(this)
        //初始化Toast框架
        Toaster.init(this)
        //初始化今日诗词
        JinrishiciFactory.init(context)
        //全局配置侧滑返回activity(有各种骚操作的奇怪BUG，不影响正常使用)
        SwipeBack.getInstance().run {
            //初始化侧滑返回
            init(this@MyApplication)
            //栈底不可滑动返回
            isRootSwipeBackEnable = false
            //强制边缘侧滑返回(横向列表适配器)
            isSwipeBackForceEdge = true
            //右滑方向
            swipeBackDirection = SwipeBackDirection.RIGHT
            //底部activity联动视差效果
            swipeBackTransformer = ParallaxSwipeBackTransformer()
        }
        //网络请求配置全局根路径
        NetConfig.init(BaseURL) {
            //设置Gson解析方式
            setConverter(GsonConvert())
            //设置cookie管理器
            //cookieJar(CookieJarImpl(PersistentCookieStore(context)))
            cookieJar(MyCookieJar())
            //添加日志拦截器
            addInterceptor(LogRecordInterceptor(BuildConfig.DEBUG))
        }
        //全局缺省页配置 [https://github.com/liangjingkanji/StateLayout]
        StateConfig.apply {
            emptyLayout = R.layout.layout_empty
            loadingLayout = R.layout.layout_loading
            errorLayout = R.layout.layout_error
            //点击重试，会触发[onRefresh]
            setRetryIds(R.id.msg)
        }
        //全局分页初始索引(注意：每次调用智能刷新扩展布局的Refresh刷新方法都会重置索引，若请求分页初始索引不同，需在每个页面设置单例初始索引)
        PageRefreshLayout.startIndex = 0
        //全局预加载索引
        PageRefreshLayout.preloadIndex = 3
        //初始化SmartRefreshLayout构建器
        //设置全局RefreshHeader构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            MyClassicsHeader(context)
        }
        //设置全局RefreshFooter构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            MyClassicsFooter(context)
        }
    }
}