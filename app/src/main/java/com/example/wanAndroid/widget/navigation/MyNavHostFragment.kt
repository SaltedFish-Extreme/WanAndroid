package com.example.wanAndroid.widget.navigation

import android.view.View
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.example.wanAndroid.R

/**
 * 作者：何高建
 *
 * 时间：2021/6/29
 *
 * 描述：Hide - Show 自定义NavHostFragment
 */
class MyNavHostFragment : NavHostFragment() {

    /** @return 使用自己的FragmentNavigator */
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return MyFragmentNavigator(requireContext(), childFragmentManager, containerId)
    }

    private val containerId: Int
        get() {
            val id = id
            return if (id != 0 && id != View.NO_ID) {
                id
                // Fallback to using our own ID if this Fragment wasn't added via
                // add(containerViewId, Fragment)
            } else R.id.nav_host_fragment_container
        }
}