package com.example.wanAndroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.AppConfig
import com.example.wanAndroid.ui.activity.LoginActivity
import com.example.wanAndroid.ui.activity.ShareArticleActivity
import com.example.wanAndroid.widget.ext.bindViewPager2
import com.example.wanAndroid.widget.ext.init
import com.google.android.material.appbar.MaterialToolbar
import com.hjq.toast.Toaster
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 广场Fragment
 */
class SquareFragment : Fragment() {

    private val toolbar: MaterialToolbar by lazy { requireActivity().findViewById(R.id.toolbar) }
    private val viewPager: ViewPager2 by lazy { requireView().findViewById(R.id.view_pager) }
    private val magicIndicator: MagicIndicator by lazy { requireView().findViewById(R.id.magic_indicator) }
    private val toolbarChild: androidx.appcompat.widget.Toolbar by lazy { requireView().findViewById(R.id.include_viewpager_toolbar) }

    /** fragment集合 */
    private val fragments: ArrayList<Fragment> by lazy { arrayListOf() }

    /** 分类集合 */
    private val classifyList: ArrayList<String> by lazy { arrayListOf("广场", "每日一问", "体系", "导航", "教程") }

    init {
        //将子fragment添加进集合
        fragments.add(SquareChildFragment())
        fragments.add(InquiryAnswerChildFragment())
        fragments.add(SystemFragment())
        fragments.add(NavigationFragment())
        fragments.add(CourseChildFragment())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarChild.run {
            //填充子toolbar右侧菜单项
            inflateMenu(R.menu.menu_toolbar_child_add)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    //添加按钮要执行的操作
                    R.id.add -> {
                        if (AppConfig.UserName.isEmpty()) {
                            Toaster.show(getString(R.string.please_login))
                            startActivityForResult(Intent(context, LoginActivity::class.java), 0, null)
                        } else {
                            openActivity<ShareArticleActivity>()
                        }
                    }
                }
                true
            }
        }
        //初始化viewpager2
        viewPager.init(this@SquareFragment, fragments)
        //初始化MagicIndicator
        magicIndicator.bindViewPager2(viewPager, classifyList) {
            //如果是第一个子项(广场)，显示toolbar右侧菜单，否则隐藏
            toolbarChild.menu.findItem(R.id.add).isVisible = it == 0
        }
        //缓存所有fragment，不会销毁重建
        viewPager.offscreenPageLimit = fragments.size
        //将viewpager指示器居中左对齐(留出添加按钮的位置)
        val params = magicIndicator.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER_VERTICAL
        magicIndicator.layoutParams = params
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = getString(R.string.square_fragment)
    }
}