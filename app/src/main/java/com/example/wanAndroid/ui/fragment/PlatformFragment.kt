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
import com.example.wanAndroid.widget.toolbar.Toolbar
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 公众号Fragment
 */
class PlatformFragment : Fragment() {

    private val toolbar: Toolbar by lazy { requireActivity().findViewById(R.id.toolbar) }
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
        toolbar.title = getString(R.string.platform_fragment)
        if (first) {
            //初次恢复页面执行操作，修复|已创建过页面|切换主题后|不再执行此操作的BUG
            //(切换主题后会重建所有页面，立即执行一遍onViewCreated回调，此时不应在onViewCreated执行此操作)
            scopeNetLife {
                //获取公众号列表数据
                val mPlatformListData = Get<ApiResponse<ArrayList<ClassificationResponse>>>(NetApi.PlatformListAPI).await()
                //添加所有列表tab
                classifyList.addAll(mPlatformListData.data.map { it.name })
                //遍历所有列表数据，根据id创建对应的fragment实例添加
                mPlatformListData.data.forEach {
                    fragments.add(PlatformChildFragment.newInstance(it.id))
                }
                //初始化viewpager2
                viewPager.init(this@PlatformFragment, fragments)
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