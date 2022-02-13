package com.example.wanAndroid.widget.dialog

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.example.wanAndroid.R

/**
 * Created by 咸鱼至尊 on 2022/2/8
 *
 * desc: 对话框辅助类,需要自己调用show方法
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object Dialog {

    /** 获取一个Dialog */
    fun getDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
    }

    /** 获取一个耗时的对话框 ProgressDialog */
    @Suppress("DEPRECATION")
    fun getWaitDialog(context: Context, message: String): ProgressDialog {
        val waitDialog = ProgressDialog(context)
        if (!TextUtils.isEmpty(message)) {
            waitDialog.setMessage(message)
        }
        return waitDialog
    }

    /** 获取一个信息对话框,注意需要自己手动调用show方法 */
    @JvmOverloads
    fun getMessageDialog(
        context: Context, message: String,
        onClickListener: DialogInterface.OnClickListener? = null
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.sure), onClickListener)
        return builder
    }

    /** 获取一个确认对话框 */
    fun getConfirmDialog(
        context: Context, message: String,
        onClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.sure), onClickListener)
        builder.setNegativeButton(context.getString(R.string.cancel), null)
        return builder
    }

    fun getConfirmDialog(
        context: Context,
        message: String,
        onOKClickListener: DialogInterface.OnClickListener,
        onCancelClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.sure), onOKClickListener)
        builder.setNegativeButton(context.getString(R.string.cancel), onCancelClickListener)
        return builder
    }

    /** 获取一个选择对话框 */
    fun getSelectDialog(
        context: Context, title: String, arrays: Array<String>,
        onClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setItems(arrays, onClickListener)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.setNegativeButton(context.getString(R.string.cancel), null)
        return builder
    }

    fun getSelectDialog(
        context: Context, arrays: Array<String>,
        onClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        return getSelectDialog(context, "", arrays, onClickListener)
    }

    /** 获取一个单选的对话框 */
    fun getSingleChoiceDialog(
        context: Context,
        title: String,
        arrays: Array<String>,
        selectIndex: Int,
        onClickListener: DialogInterface.OnClickListener,
        onOKClickListener: DialogInterface.OnClickListener,
        onCancelClickListener: DialogInterface.OnClickListener? = null
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.setPositiveButton(context.getString(R.string.sure), onOKClickListener)
        builder.setNegativeButton(context.getString(R.string.cancel), onCancelClickListener)
        return builder
    }

    fun getSingleChoiceDialog(
        context: Context, title: String, arrays: Array<String>,
        selectIndex: Int, onClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.setPositiveButton(context.getString(R.string.cancel), null)
        return builder
    }

    fun getSingleChoiceDialog(
        context: Context, arrays: Array<String>, selectIndex: Int,
        onClickListener: DialogInterface.OnClickListener, onOKClickListener: DialogInterface.OnClickListener,
        onCancelClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        return getSingleChoiceDialog(
            context, "", arrays, selectIndex, onClickListener, onOKClickListener,
            onCancelClickListener
        )
    }

    /** 获取一个多选的对话框 */
    fun getMultiChoiceDialog(
        context: Context,
        title: String,
        arrays: Array<String>,
        checkedItems: BooleanArray,
        onMultiChoiceClickListener: DialogInterface.OnMultiChoiceClickListener,
        onOKClickListener: DialogInterface.OnClickListener,
        onCancelClickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setMultiChoiceItems(arrays, checkedItems, onMultiChoiceClickListener)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.setPositiveButton(context.getString(R.string.sure), onOKClickListener)
        builder.setNegativeButton(context.getString(R.string.cancel), onCancelClickListener)
        return builder
    }

}