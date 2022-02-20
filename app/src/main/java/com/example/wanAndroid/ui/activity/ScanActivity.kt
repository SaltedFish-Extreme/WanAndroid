package com.example.wanAndroid.ui.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
import com.example.wanAndroid.R
import com.example.wanAndroid.ui.base.BaseActivity
import com.example.wanAndroid.util.GlideEngine
import com.example.wanAndroid.ext.vibration
import com.example.wanAndroid.widget.view.FloatActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hjq.toast.ToastUtils
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo

/**
 * Created by 咸鱼至尊 on 2022/2/12
 *
 * desc: 扫描二维码Activity
 */
class ScanActivity : BaseActivity(), QRCodeView.Delegate {

    private val zXingView: ZXingView by lazy { findViewById(R.id.zxing_view) }
    private val fab: FloatActionButton by lazy { findViewById(R.id.fab) }

    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        val flash = findViewById<FloatingActionButton>(R.id.flash)
        //闪光灯按钮点击监听
        flash.setOnClickListener {
            //选中取反
            isChecked = !isChecked
            if (isChecked) {
                //选中打开闪光灯切换图片
                flash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash_on))
                zXingView.openFlashlight()
            } else {
                //未选中关闭闪光灯切换图片
                flash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash_off))
                zXingView.closeFlashlight()
            }
        }
        //相册按钮点击监听
        fab.setOnClickListener {
            //使用Glide图片加载引擎
            GlideEngine.instance?.let { its ->
                //参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎]
                EasyPhotos.createAlbum(this, true, false, its)
                    //参数说明：见下方`FileProvider的配置`
                    .setFileProviderAuthority("com.example.wanAndroid.ui.provider.FileProvider")
                    //无拼图功能
                    .setPuzzleMenu(false)
                    //参数说明：最大可选数，默认1
                    .setCount(1)
                    //相册回调
                    .start(object : SelectCallback() {
                        //photos:返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                        override fun onResult(photos: ArrayList<Photo>, isOriginal: Boolean) {
                            photos.forEach {
                                //根据图片路径解析二维码
                                zXingView.decodeQRCode(it.path)
                            }
                        }

                        override fun onCancel() {}
                    })
            }
        }
        zXingView.setDelegate(this)
    }

    override fun onStart() {
        super.onStart()
        zXingView.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
        zXingView.startSpotAndShowRect() // 显示扫描框，并开始识别
    }

    override fun onStop() {
        zXingView.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onDestroy() {
        zXingView.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }

    override fun onScanQRCodeSuccess(result: String?) {
        //解析失败
        if (result.isNullOrEmpty()) {
            ToastUtils.show(R.string.identify_error)
            zXingView.startSpot()//继续识别
            return
        }
        ToastUtils.show(R.string.identify_succeed)
        vibration()
        WebActivity.start(this, result)
        finish()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        var tipText = zXingView.scanBoxView.tipText
        val ambientBrightnessTip = getString(R.string.open_flash)
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                zXingView.scanBoxView.tipText = tipText + ambientBrightnessTip
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                zXingView.scanBoxView.tipText = tipText
            }
        }
    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtils.show(R.string.open_camera_failed)
    }
}