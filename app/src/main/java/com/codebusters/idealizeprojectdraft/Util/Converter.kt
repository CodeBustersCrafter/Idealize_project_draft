package com.codebusters.idealizeprojectdraft.Util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

class Converter {
    @Suppress("DEPRECATION")
    fun getBitmap(uri: Uri, context : Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // For Android 10+ (API level 29+)
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            // For earlier versions
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }
}