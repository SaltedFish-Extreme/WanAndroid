package com.example.wanAndroid.ui.activity

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.example.wanAndroid.R
import com.example.wanAndroid.logic.dao.HistoryRecordDB
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.widget.dialog.Dialog
import com.example.wanAndroid.widget.ext.cancelFloatBtn
import com.example.wanAndroid.widget.ext.html2Spanned
import com.example.wanAndroid.widget.ext.html2String
import com.example.wanAndroid.widget.ext.initFloatBtn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import org.litepal.LitePal
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import per.goweii.swipeback.SwipeBackAbility

/**
 * Created by 咸鱼至尊 on 2022/2/18
 *
 * desc: 历史记录页Activity (这里为了侧滑删除适配器数据，不再使用BRVAH框架填充适配器，而是使用BRV)
 */
class HistoryRecordActivity : BaseActivity(), SwipeBackAbility.OnlyEdge {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_record)
        //初始化rv悬浮按钮扩展函数
        rv.initFloatBtn(fab)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        //标题栏右侧副标题点击删除所有历史记录
        titleBar.rightView.setOnClickListener {
            Dialog.getConfirmDialog(this, getString(R.string.delete_confirm)) { _, _ ->
                //清空适配器
                rv.adapter = null
                //同时从数据库删除对应表的所有记录
                LitePal.deleteAll<HistoryRecordDB>()
                ToastUtils.show(getString(R.string.delete_succeed))
            }.show()
        }
        //代码中创建适配器列表，线性布局
        rv.linear().setup {
            //设置数据类型及item布局
            addType<HistoryRecordDB>(R.layout.item_history_record_list)
            //数据绑定回调
            onBind {
                //设置标题
                findView<TextView>(R.id.item_history_title).text = getModel<HistoryRecordDB>().title.html2Spanned()
                //设置日期
                findView<TextView>(R.id.item_history_date).text = DateFormat.format("yyyy-MM-dd HH:mm:ss", getModel<HistoryRecordDB>().date)
            }
            //点击item跳转网页
            R.id.item_history.onClick { WebActivity.start(this@HistoryRecordActivity, getModel<HistoryRecordDB>().url) }
            //侧滑回调
            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback() {
                // 这里可以重写函数
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    // 这是侧滑删除后回调, 这里可以同步服务器 (这里从数据库中删除对应行)
                    LitePal.deleteAll<HistoryRecordDB>(
                        "title = ?",
                        ((viewHolder as BindingAdapter.BindingViewHolder).getModel<HistoryRecordDB>().title.html2String())
                    )
                    ToastUtils.show(getString(R.string.delete_succeed))
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        //恢复页面查询出所有历史记录数据，并设置给rv的数据模型，自动刷新数据
        rv.models = LitePal.order("date desc").find<HistoryRecordDB>()
    }

    override fun onDestroy() {
        super.onDestroy()
        rv.cancelFloatBtn(fab)
    }

    /** 只允许边缘侧滑返回 */
    override fun swipeBackOnlyEdge() = true
}