package com.example.wanAndroid.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.SystemResponse
import com.example.wanAndroid.ui.base.BaseAdapter
import com.example.wanAndroid.widget.ext.html2Sting
import com.google.android.flexbox.FlexboxLayoutManager
import com.hjq.toast.ToastUtils

/**
 * Created by 咸鱼至尊 on 2022/2/2
 *
 * desc: 自定义体系适配器
 */
class SystemAdapter(dataList: MutableList<SystemResponse>) : BaseAdapter<SystemResponse>(R.layout.item_system_list, dataList) {

    init {
        //设置默认加载动画
        setAnimationWithDefault(AnimationType.ScaleIn)
        //设置Item点击事件
        this.setOnItemClickListener { _, _, position -> ToastUtils.debugShow("我被点击了！ ${dataList[position].name}") }
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        //获取rv
        viewHolder.getView<RecyclerView>(R.id.rv).run {
            //使用伸缩布局
            layoutManager = FlexboxLayoutManager(context)
            //避免item改变重新绘制rv
            setHasFixedSize(true)
            //禁用嵌套滚动
            isNestedScrollingEnabled = false
        }
    }

    override fun convert(holder: BaseViewHolder, item: SystemResponse) {
        //设置rv标题
        holder.setText(R.id.title, item.name.html2Sting())
        //使用子流标签适配器
        holder.getView<RecyclerView>(R.id.rv).adapter = SystemChildAdapter(item.children)
    }
}