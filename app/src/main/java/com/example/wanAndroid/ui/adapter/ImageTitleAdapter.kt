package com.example.wanAndroid.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.model.BannerResponse
import com.example.wanAndroid.ui.activity.WebActivity
import com.youth.banner.adapter.BannerAdapter

/**
 * Created by 咸鱼至尊 on 2021/12/20
 *
 * desc: 自定义banner布局适配器，图片+标题
 */
class ImageTitleAdapter(dataList: List<BannerResponse>) : BannerAdapter<BannerResponse, ImageTitleAdapter.ImageTitleHolder>(dataList) {

    inner class ImageTitleHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.banner_title)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageTitleHolder {
        return ImageTitleHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_banner_image_title, parent, false))
    }

    override fun onBindView(holder: ImageTitleHolder, data: BannerResponse, position: Int, size: Int) {
        holder.imageView.run {
            //加载网络图片
            Glide.with(context).load(data.imagePath).into(this)
            //设置标题
            holder.title.text = data.title
            //banner点击事件
            setOnBannerListener { mData, _ ->
                //打开网页URL
                WebActivity.start(context, mData.url)
            }
        }
    }
}