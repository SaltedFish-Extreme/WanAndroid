package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.drake.serialize.intent.bundle
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.ui.fragment.SystemChildFragment
import com.example.wanAndroid.widget.ext.bindViewPager2
import com.example.wanAndroid.widget.ext.init
import com.google.android.material.appbar.MaterialToolbar
import com.gyf.immersionbar.ktx.immersionBar
import net.lucode.hackware.magicindicator.MagicIndicator
import per.goweii.swipeback.SwipeBackAbility

/**
 * Created by 咸鱼至尊 on 2022/2/15
 *
 * desc: 体系页Activity
 */
class SystemActivity : BaseActivity(), SwipeBackAbility.OnlyEdge {

    private val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    private val viewPager: ViewPager2 by lazy { findViewById(R.id.view_pager) }
    private val magicIndicator: MagicIndicator by lazy { findViewById(R.id.magic_indicator) }

    /** Serialize界面传递参数: title */
    private val title: String by bundle()

    /** Serialize界面传递参数: content */
    private val content: List<String> by bundle()

    /** Serialize界面传递参数: cid */
    private val cid: List<Int> by bundle()

    /** Serialize界面传递参数: index 默认值0 */
    private val index: Int by bundle(0)

    /** fragment集合 */
    private val fragments: ArrayList<Fragment> by lazy { arrayListOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system)
        //使标题栏和状态栏不重叠
        immersionBar {
            titleBar(toolbar)
        }
        toolbar.apply {
            //设置标题
            title = this@SystemActivity.title
            //使用toolBar并使其外观与功能和actionBar一致
            setSupportActionBar(this)
            //使用默认导航按钮
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //点击导航按钮关闭当前页面
            setNavigationOnClickListener { finish() }
            //拦截导航按钮长按吐司
            navigationContentDescription = ""
        }
        //没有子页面不继续执行后续操作
        if (content.isEmpty()) return
        //根据cid创建对应的fragment实例并添加进集合
        cid.forEach {
            fragments.add(SystemChildFragment.newInstance(it))
        }
        //初始化viewpager2
        viewPager.init(this@SystemActivity, fragments)
        //初始化MagicIndicator
        magicIndicator.bindViewPager2(viewPager, content)
        //缓存所有fragment，不会销毁重建
        viewPager.offscreenPageLimit = fragments.size
        //指定viewpager默认选中页面
        viewPager.currentItem = index
    }

    /** 只允许边缘侧滑返回 */
    override fun swipeBackOnlyEdge() = true
}