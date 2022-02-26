package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.ui.fragment.CollectArticleFragment
import com.example.wanAndroid.ui.fragment.CollectURLFragment
import com.example.wanAndroid.widget.ext.bindViewPager2
import com.example.wanAndroid.widget.ext.init
import com.example.wanAndroid.widget.toolbar.Toolbar
import net.lucode.hackware.magicindicator.MagicIndicator
import per.goweii.swipeback.SwipeBackAbility

/**
 * Created by 咸鱼至尊 on 2022/2/26
 *
 * desc: 我的收藏页Activity
 */
class CollectActivity : BaseActivity(), SwipeBackAbility.OnlyEdge {

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val viewPager: ViewPager2 by lazy { findViewById(R.id.view_pager) }
    private val magicIndicator: MagicIndicator by lazy { findViewById(R.id.magic_indicator) }

    /** fragment集合 */
    private val fragments: ArrayList<Fragment> by lazy { arrayListOf() }

    /** 分类集合 */
    private val classifyList: ArrayList<String> by lazy { arrayListOf("文章", "网址") }

    init {
        //将子fragment添加进集合
        fragments.add(CollectArticleFragment())
        fragments.add(CollectURLFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system)
        toolbar.apply {
            //设置标题
            title = getString(R.string.my_collect)
            //使用toolBar并使其外观与功能和actionBar一致
            setSupportActionBar(this)
            //使用默认导航按钮
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //点击导航按钮关闭当前页面
            setNavigationOnClickListener { finish() }
            //拦截导航按钮长按吐司
            navigationContentDescription = ""
        }
        //初始化viewpager2
        viewPager.init(this@CollectActivity, fragments)
        //初始化MagicIndicator
        magicIndicator.bindViewPager2(viewPager, classifyList)
        //缓存所有fragment，不会销毁重建
        viewPager.offscreenPageLimit = fragments.size
    }

    /** 只允许边缘侧滑返回 */
    override fun swipeBackOnlyEdge() = true
}