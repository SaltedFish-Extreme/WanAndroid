package com.example.wanAndroid.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by 咸鱼至尊 on 2022/2/12
 *
 * desc: 图片保存工具类
 */
@Suppress("unused", "DEPRECATION")
object PhotoUtils {

    fun saveBitmap2Gallery(context: Context, bitmap: Bitmap): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //返回出一个URI
            val insert = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                //这里如果不写的话 默认是保存在 /sdCard/Pictures
                ContentValues()//这里可以啥也不设置 保存图片默认就好了
            ) ?: return false //为空的话 直接失败返回了
            //这个打开了输出流  直接保存图片就好了
            context.contentResolver.openOutputStream(insert).use {
                it ?: return false
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            return true
        } else {
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "title", "desc")
            return true
        }
    }

    fun saveBitmap2Gallery2(context: Context, bitmap: Bitmap): Boolean {
        val name = System.currentTimeMillis().toString()
        val photoPath = Environment.DIRECTORY_DCIM + "/Camera"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, photoPath)//保存路径
                put(MediaStore.MediaColumns.IS_PENDING, true)
            }
        }
        val insert = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ) ?: return false //为空的话 直接失败返回了
        //这个打开了输出流  直接保存图片就好了
        context.contentResolver.openOutputStream(insert).use {
            it ?: return false
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, false)
        }
        return true
    }

    fun saveFile2Gallery(context: Context, url: String): Boolean {
        //返回出一个URI
        val insert = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            //这里可以默认不写 保存在默认位置
            ContentValues()
        ) ?: return false //为空的话 直接失败返回了
        //这个打开了输出流  直接保存图片就好了
        context.contentResolver.openOutputStream(insert).use { os ->
            os ?: return false
            return download(url, os)
        }
    }

    fun saveFile2Gallery2(context: Context, url: String): Boolean {
        val name = System.currentTimeMillis().toString()
        val photoPath = Environment.DIRECTORY_DCIM + "/Camera"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, photoPath)//保存路径
                put(MediaStore.MediaColumns.IS_PENDING, true)
            }
        }
        //返回出一个URI
        val insert = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ) ?: return false
        //这个打开了输出流  直接保存图片就好了
        context.contentResolver.openOutputStream(insert).use { os ->
            os ?: return false
            val x = download(url, os)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, false)
            }
            return x
        }
    }

    private fun download(url: String, os: OutputStream): Boolean {
        val urls = URL(url)
        (urls.openConnection() as HttpURLConnection).also { conn ->
            conn.requestMethod = "GET"
            conn.connectTimeout = 5 * 1000
            return if (conn.responseCode == 200) {
                conn.inputStream.use { ins ->
                    val buf = ByteArray(2048)
                    var len: Int
                    while (ins.read(buf).also { len = it } != -1) {
                        os.write(buf, 0, len)
                    }
                    os.flush()
                }
                true
            } else {
                false
            }
        }
    }
}