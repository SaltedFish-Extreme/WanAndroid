package com.example.wanAndroid.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment
import com.example.wanAndroid.R

/**
 * Created by 咸鱼至尊 on 2022/2/13
 *
 * desc: 关于我们DialogFragment
 */
class AboutDialogFragment : DialogFragment(R.layout.fragment_dialog_about) {

    override fun onStart() {
        super.onStart()
        //背景透明
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}