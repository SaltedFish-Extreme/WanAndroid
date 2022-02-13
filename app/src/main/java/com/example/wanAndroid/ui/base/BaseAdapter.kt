package com.example.wanAndroid.ui.base

import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: BaseRecyclerViewAdapterHelper adapter基类
 */
abstract class BaseAdapter<T>(@LayoutRes private val layoutResId: Int, data: MutableList<T>? = null) :
    BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data)