package com.example.wanAndroid.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.example.wanAndroid.R

/**
 * Created by 咸鱼至尊 on 2022/1/24
 *
 * desc: CoordinatorLayout嵌套RecyclerView联动布局行为(内容布局)
 */
@Suppress("unused")
class NestedContentScrollBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {
    private var headerHeight = 0
    private lateinit var contentView: View // 其实就是 RecyclerView
    private var scroller: OverScroller? = null
    private val scrollRunnable = object : Runnable {
        override fun run() {
            scroller?.let { scroller ->
                if (scroller.computeScrollOffset()) {
                    contentView.translationY = scroller.currY.toFloat()
                    ViewCompat.postOnAnimation(contentView, this)
                }
            }
        }
    }

    private fun startAutoScroll(current: Int, target: Int, duration: Int) {
        if (scroller == null) {
            scroller = OverScroller(contentView.context)
        }
        if (scroller!!.isFinished) {
            contentView.removeCallbacks(scrollRunnable)
            scroller!!.startScroll(0, current, 0, target - current, duration)
            ViewCompat.postOnAnimation(contentView, scrollRunnable)
        }
    }

    private fun stopAutoScroll() {
        scroller?.let {
            if (!it.isFinished) {
                it.abortAnimation()
                contentView.removeCallbacks(scrollRunnable)
            }
        }
    }


    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        contentView = child
        // 首先让父布局按照标准方式解析
        parent.onLayoutChild(child, layoutDirection)
        // 获取到 HeaderView 的高度
        headerHeight = parent.findViewById<View>(R.id.banner).height
        // 设置 top 从而排在 HeaderView的下面
        ViewCompat.offsetTopAndBottom(child, headerHeight)
        return true // true 表示我们自己完成了解析 不要再自动解析了
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int
    ): Boolean {
        // 如果是垂直滑动的话就声明需要处理
        // 只有这里返回 true 才会收到下面一系列滑动事件的回调
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        if (child.translationY >= 0f || child.translationY <= -headerHeight) {
            // RV 已经归位（完全折叠或完全展开）
            return
        }
        if (child.translationY <= -headerHeight * 0.5f) {
            stopAutoScroll()
            startAutoScroll(child.translationY.toInt(), -headerHeight, 1000)
        } else {
            stopAutoScroll()
            startAutoScroll(child.translationY.toInt(), 0, 600)
        }
    }


    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int
    ) {
        // 此时 RecyclerView 还没开始滑动
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        stopAutoScroll()
        if (dy > 0) { // 只处理手指上滑
            val newTransY = child.translationY - dy
            if (newTransY >= -headerHeight) {
                // 完全消耗滑动距离后没有完全贴顶或刚好贴顶
                // 那么就声明消耗所有滑动距离，并上移 RecyclerView
                consumed[1] = dy // consumed[0/1] 分别用于声明消耗了x/y方向多少滑动距离
                child.translationY = newTransY
            } else {
                // 如果完全消耗那么会导致 RecyclerView 超出可视区域
                // 那么只消耗恰好让 RecyclerView 贴顶的距离
                consumed[1] = headerHeight + child.translationY.toInt()
                child.translationY = -headerHeight.toFloat()
            }
        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        // 此时 RV 已经完成了滑动，dyUnconsumed 表示剩余未消耗的滑动距离
        super.onNestedScroll(
            coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed
        )
        stopAutoScroll()
        if (dyUnconsumed < 0) { // 只处理手指向下滑动的情况
            val newTransY = child.translationY - dyUnconsumed
            if (newTransY <= 0) {
                child.translationY = newTransY
            } else {
                child.translationY = 0f
            }
        }
    }
}