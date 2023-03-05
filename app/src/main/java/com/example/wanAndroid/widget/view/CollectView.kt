package com.example.wanAndroid.widget.view

import android.content.Context
import android.util.AttributeSet
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.logic.dao.AppConfig
import com.hjq.toast.Toaster
import per.goweii.reveallayout.RevealLayout

/**
 * Created by 咸鱼至尊 on 2022/2/28
 *
 * desc: 收藏视图
 */
class CollectView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RevealLayout(context, attrs, defStyleAttr) {

    private lateinit var mOnClickListener: OnClickListener

    override fun initAttr(attrs: AttributeSet) {
        super.initAttr(attrs)
        setCheckWithExpand(true)
        setUncheckWithExpand(false)
        setAnimDuration(400)
        setAllowRevert(false)
    }

    override fun getCheckedLayoutId() = R.layout.view_reveal_like_checked

    override fun getUncheckedLayoutId() = R.layout.view_reveal_like_unchecked

    fun setOnClickListener(onClickListener: OnClickListener) {
        mOnClickListener = onClickListener
        setOnClickListener {
            if (AppConfig.UserName.isNotEmpty()) {
                //登陆过直接走点击事件回调
                mOnClickListener.onClick(this@CollectView)
                context.vibration() //震动一下
            } else {
                //否则弹吐司并且不给选中
                Toaster.show(R.string.please_login)
                isChecked = false
            }
        }
    }

    interface OnClickListener {
        fun onClick(v: CollectView)
    }
}