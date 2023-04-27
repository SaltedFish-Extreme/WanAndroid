package com.example.wanAndroid.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.text.TextUtils
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import java.util.Random

/**
 * 动画帮助类
 *
 * @author CuiZhen
 * @version v1.0.0
 * @date 2018/3/30-上午10:51
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object AnimatorUtil {
    /**
     * 改变一个View的宽度和高度
     *
     * @param target 目标View
     * @param toWidth 目标宽度
     * @param toHeight 目标高度
     * @param duration 时长
     * @param interpolator 时间插值器默认为DecelerateInterpolator
     */
    fun changeLargeViewSize(target: View, toWidth: Int, toHeight: Int, duration: Long, interpolator: Interpolator?) {
        val params = target.layoutParams
        val toX = toWidth.toFloat() / params.width.toFloat()
        val toY = toHeight.toFloat() / params.height.toFloat()
        val scaleAnimation = ScaleAnimation(1F, toX, 1F, toY, 0F, 0F)
        scaleAnimation.duration = duration
        scaleAnimation.fillAfter = true
        scaleAnimation.interpolator = interpolator ?: DecelerateInterpolator()
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val scaleAnimationX = ScaleAnimation(1F, 1F, 1F, 1F, 0F, 0F)
                scaleAnimationX.duration = 0
                scaleAnimationX.fillAfter = true
                target.startAnimation(scaleAnimationX)
                params.width = toWidth
                params.height = toHeight
                target.requestLayout()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        target.startAnimation(scaleAnimation)
    }

    /**
     * 改变一个View的宽度和高度
     *
     * @param target 目标View
     * @param toWidth 目标宽度
     * @param toHeight 目标高度
     * @param duration 时长
     * @param interpolator 时间插值器默认为DecelerateInterpolator
     */
    fun changeViewSize(target: View, toWidth: Int, toHeight: Int, duration: Long, interpolator: TimeInterpolator?) {
        val params = target.layoutParams
        val fromWidth = params.width
        val fromHeight = params.height
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator ?: DecelerateInterpolator()
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            val f = animator.animatedValue as Float
            val nowWidth = fromWidth + (toWidth - fromWidth) * f
            val nowHeight = fromHeight + (toHeight - fromHeight) * f
            params.width = nowWidth.toInt()
            params.height = nowHeight.toInt()
            target.requestLayout()
        }
        valueAnimator.start()
    }

    /**
     * 改变一个View的宽度
     *
     * @param view 需要该表的View
     * @param width 宽度
     * @param duration 时长
     */
    fun changeViewWidth(view: View, width: Int, duration: Long, interpolator: TimeInterpolator?) {
        val valueAnimator = ValueAnimator.ofInt(view.layoutParams.width, width)
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator ?: DecelerateInterpolator()
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            view.layoutParams.width = animator.animatedValue as Int
            view.requestLayout()
        }
        valueAnimator.start()
    }

    /**
     * 改变一个View的高度
     *
     * @param view 需要该表的View
     * @param height 高度
     * @param duration 时长
     */
    fun changeViewHeight(view: View, height: Int, duration: Long, interpolator: TimeInterpolator?) {
        val lastH = view.height
        val valueAnimator = ValueAnimator.ofInt(lastH, height)
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator ?: DecelerateInterpolator()
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            view.layoutParams.height = animator.animatedValue as Int
            view.requestLayout()
        }
        valueAnimator.start()
    }

    /**
     * 旋转的动画
     *
     * @param target 需要选择的View
     * @param fromDegrees 初始的角度【从这个角度开始】
     * @param toDegrees 当前需要旋转的角度【转到这个角度来】
     */
    fun rotateCenter(target: View, fromDegrees: Float, toDegrees: Float, duration: Long) {
        val centerX = target.width / 2.0f
        val centerY = target.height / 2.0f
        //这个是设置需要旋转的角度（也是初始化），我设置的是当前需要旋转的角度
        val rotateAnimation = RotateAnimation(fromDegrees, toDegrees, centerX, centerY)
        //这个是设置动画时间的
        rotateAnimation.duration = duration
        //动画执行完毕后是否停在结束时的角度上
        rotateAnimation.fillAfter = true
        rotateAnimation.interpolator = DecelerateInterpolator()
        //启动动画
        target.startAnimation(rotateAnimation)
    }

    fun translationX(view: View?, start: Int, end: Int, duration: Long) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", start.toFloat(), end.toFloat())
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun translationY(view: View?, start: Int, end: Int, duration: Long) {
        val animator = ObjectAnimator.ofFloat(view, "translationY", start.toFloat(), end.toFloat())
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun doBigDecimalAnim(target: TextView?, from: BigDecimal?, to: BigDecimal?, scaleCount: Int, duration: Long) {
        val animator = ValueAnimator.ofObject(TypeEvaluator { fraction: Float, startValue: BigDecimal, endValue: BigDecimal ->
            startValue.add(
                endValue.subtract(startValue).multiply(
                    BigDecimal(fraction.toDouble())
                )
            )
        } as TypeEvaluator<BigDecimal>, from, to)
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as BigDecimal
            if (target != null) {
                target.text = String.format(value.setScale(scaleCount, RoundingMode.DOWN).toString())
            }
        }
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun doBigDecimalAnim(target: TextView, to: BigDecimal?, scaleCount: Int, duration: Long) {
        val from: BigDecimal = try {
            BigDecimal(target.text.toString())
        } catch (e: NumberFormatException) {
            BigDecimal(0)
        }
        doBigDecimalAnim(target, from, to, scaleCount, duration)
    }

    fun doBigDecimalAnim(target: TextView, scaleCount: Int, duration: Long) {
        val tob: BigDecimal = try {
            BigDecimal(target.text.toString())
        } catch (e: NumberFormatException) {
            BigDecimal(0)
        }
        doBigDecimalAnim(target, tob, scaleCount, duration)
    }

    fun doFloatAnim(target: TextView?, from: Float, to: Float, scaleCount: Int, duration: Long) {
        val animator = ValueAnimator.ofFloat(from, to)
        val format = "%." + scaleCount + "f"
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            if (target != null) {
                target.text = String.format(format, value)
            }
        }
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun doFloatAnim(target: TextView, to: Float, scaleCount: Int, duration: Long) {
        val from: Float = try {
            target.text.toString().toFloat()
        } catch (e: NumberFormatException) {
            0f
        }
        doFloatAnim(target, from, to, scaleCount, duration)
    }

    fun doFloatAnim(target: TextView, to: String, scaleCount: Int, duration: Long) {
        val tof: Float = try {
            to.toFloat()
        } catch (e: NumberFormatException) {
            0f
        }
        doFloatAnim(target, tof, scaleCount, duration)
    }

    fun doIntAnim(target: TextView?, from: Int, to: Int, duration: Long) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Int
            if (target != null) {
                target.text = String.format(Locale.CHINA, "%d", value)
            }
        }
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun doIntAnim(target: TextView, to: Int, duration: Long) {
        val fromStr = target.text.toString()
        val from: Int = try {
            fromStr.toInt()
        } catch (e: NumberFormatException) {
            0
        }
        doIntAnim(target, from, to, duration)
    }

    fun doIntAnim(target: TextView, duration: Long) {
        val toi: Int = try {
            target.text.toString().toInt()
        } catch (e: NumberFormatException) {
            0
        }
        doIntAnim(target, toi, duration)
    }

    fun doStringAnim(target: TextView?, from: String?, to: String?, duration: Long) {
        if (from == null || to == null) {
            return
        }
        if (TextUtils.equals(from, to)) {
            return
        }
        val animator = ValueAnimator.ofObject(object : TypeEvaluator<String> {
            private var startLength = 0
            private var endLength = 0
            private val baseBuilder = StringBuilder()
            private val random = Random()
            override fun evaluate(fraction: Float, startValue: String, endValue: String): String {
                if (fraction == 0f) {
                    startLength = startValue.length
                    endLength = endValue.length
                    val chars = (startValue + endValue).toCharArray()
                    for (c in chars) {
                        if (baseBuilder.indexOf(c.toString()) >= 0) {
                            continue
                        }
                        when (c) {
                            in '0'..'9' -> {
                                baseBuilder.append("0123456789")
                            }

                            in 'a'..'z' -> {
                                baseBuilder.append("abcdefghijklmnopqrstuvwxyz")
                            }

                            in 'A'..'Z' -> {
                                baseBuilder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                            }

                            else -> {
                                baseBuilder.append(c)
                            }
                        }
                    }
                }
                if (baseBuilder.isEmpty()) {
                    return endValue
                }
                if (fraction == 1f) {
                    return endValue
                }
                val length = (startLength + (endLength - startLength) * fraction).toInt()
                val stringBuilder = StringBuilder()
                for (i in 0 until length) {
                    val number = random.nextInt(baseBuilder.length)
                    stringBuilder.append(baseBuilder[number])
                }
                return stringBuilder.toString()
            }
        }, from, to)
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as String
            if (target != null) {
                target.text = value
            }
        }
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun flipHorizontal(target: View, oldView: View, newView: View, time: Long, downUp: Boolean) {
        val tag = target.tag
        if (tag is String && TextUtils.equals("flipHorizontal", tag)) {
            return
        }
        val from1: Float
        val to1: Float
        val from2: Float
        val to2: Float
        if (downUp) {
            from1 = target.rotationX
            to1 = 90f
            from2 = -90f
            to2 = 0f
        } else {
            from1 = target.rotationX
            to1 = -90f
            from2 = 90f
            to2 = 0f
        }
        val animator1 = ObjectAnimator.ofFloat(target, "rotationX", from1, to1).setDuration(time)
        animator1.interpolator = AccelerateInterpolator(2.0f)
        val animator2 = ObjectAnimator.ofFloat(target, "rotationX", from2, to2).setDuration(time)
        animator2.interpolator = OvershootInterpolator(2.0f)
        animator1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                oldView.visibility = View.GONE
                newView.visibility = View.VISIBLE
                animator2.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        target.tag = "flipHorizontal"
        animator1.start()
    }

    fun flipVertical(target: View, oldView: View, newView: View, time: Long, cameraDistance: Float, leftRight: Boolean) {
        val tag = target.tag
        if (tag is String && TextUtils.equals("flipVertical", tag)) {
            return
        }
        val from1: Float
        val to1: Float
        val from2: Float
        val to2: Float
        if (leftRight) {
            from1 = target.rotationY
            to1 = 90f
            from2 = -90f
            to2 = 0f
        } else {
            from1 = target.rotationY
            to1 = -90f
            from2 = 90f
            to2 = 0f
        }
        val scale = target.context.resources.displayMetrics.density * cameraDistance
        target.cameraDistance = scale
        val animator1 = ObjectAnimator.ofFloat(target, "rotationY", from1, to1).setDuration(time)
        animator1.interpolator = AccelerateInterpolator(2.0f)
        val animator2 = ObjectAnimator.ofFloat(target, "rotationY", from2, to2).setDuration(time)
        animator2.interpolator = OvershootInterpolator(2.0f)
        animator1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                target.tag = null
                oldView.visibility = View.INVISIBLE
                newView.visibility = View.VISIBLE
                animator2.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        target.tag = "flipVertical"
        animator1.start()
    }
}