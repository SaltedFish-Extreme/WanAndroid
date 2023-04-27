package com.example.wanAndroid.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.wanAndroid.R

/**
 * Created by 咸鱼至尊 on 2022/1/24
 *
 * desc: CoordinatorLayout嵌套RecyclerView联动布局行为(头布局)
 */
@Suppress("unused")
class NestedHeaderScrollBehavior constructor(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        // child: 当前 Behavior 所关联的 View，此处是 HeaderView
        // dependency: 待判断是否需要监听的其他子 View
        return dependency.id == R.id.rv
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        child.translationY = dependency.translationY * 0.5f
        child.alpha = 1 + dependency.translationY / (child.height * 0.6f)
        // 如果改变了 child 的大小位置必须返回 true 来刷新
        return true
    }
}