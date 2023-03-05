package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.drake.serialize.intent.openActivity
import com.example.wanAndroid.R
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.util.PhotoUtils
import com.hjq.bar.TitleBar
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.Toaster
import com.huantansheng.easyphotos.ui.widget.PressedImageView

/**
 * Created by 咸鱼至尊 on 2022/2/12
 *
 * desc: 扫码下载页Activity
 */
class QRCodeActivity : BaseActivity() {

    private val titleBar: TitleBar by lazy { findViewById(R.id.title_bar) }
    private val qrCode: PressedImageView by lazy { findViewById(R.id.qr_code) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        //标题栏返回按钮关闭页面
        titleBar.leftView.setOnClickListener { finish() }
        titleBar.rightView.setOnClickListener {
            //请求权限跳转扫码页
            XXPermissions.with(this).permission(Permission.CAMERA).request { _, all ->
                if (all) {
                    openActivity<ScanActivity>()
                }
            }
        }
        qrCode.setOnLongClickListener {
            //请求权限保存二维码
            XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE).request { _, all ->
                if (all) {
                    PhotoUtils.saveBitmap2Gallery(this, qrCode.drawable.toBitmap())
                    vibration()
                    Toaster.show(R.string.save_succeed)
                }
            }
            true
        }
    }
}