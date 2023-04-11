package com.example.wanAndroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.ClassificationResponse
import com.example.wanAndroid.logic.model.base.ApiResponse
import com.example.wanAndroid.logic.net.NetApi
import com.example.wanAndroid.widget.ext.bindViewPager2
import com.example.wanAndroid.widget.ext.init
import com.google.android.material.appbar.MaterialToolbar
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 项目Fragment
 */
class ProjectFragment : Fragment() {

    private val toolbar: MaterialToolbar by lazy { requireActivity().findViewById(R.id.toolbar) }
    private val viewPager: ViewPager2 by lazy { requireView().findViewById(R.id.view_pager) }
    private val magicIndicator: MagicIndicator by lazy { requireView().findViewById(R.id.magic_indicator) }

    /** 是否初次切换页面 */
    private var first = true

    /** fragment集合 */
    private val fragments: ArrayList<Fragment> by lazy { arrayListOf() }

    /** 分类集合 */
    private val classifyList: ArrayList<String> by lazy { arrayListOf() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = getString(R.string.project_fragment)
        if (first) {
            //初次恢复页面执行操作，修复|已创建过页面|切换主题后|不再执行此操作的BUG
            //(切换主题后会重建所有页面，立即执行一遍onViewCreated回调，此时不应在onViewCreated执行此操作)
            scopeNetLife {
                //获取项目分类数据
                val mProjectClassifyData = Get<ApiResponse<ArrayList<ClassificationResponse>>>(NetApi.ProjectClassifyAPI).await()
                //先加一个最新项目tab
                classifyList.add(getString(R.string.new_project))
                //将所有分类tab加上
                classifyList.addAll(mProjectClassifyData.data.map { it.name })
                //先添加创建一个最新项目子fragment实例
                fragments.add(ProjectChildFragment.newInstance(0, true))
                //遍历所有分类数据，根据id创建对应的fragment实例添加
                mProjectClassifyData.data.forEach {
                    fragments.add(ProjectChildFragment.newInstance(it.id))
                }
                //初始化viewpager2
                viewPager.init(this@ProjectFragment, fragments)
                //初始化MagicIndicator
                magicIndicator.bindViewPager2(viewPager, classifyList)
                //缓存所有fragment，不会销毁重建
                viewPager.offscreenPageLimit = fragments.size
                //设置初次创建页面为否
                first = false
            }
        }
    }
}