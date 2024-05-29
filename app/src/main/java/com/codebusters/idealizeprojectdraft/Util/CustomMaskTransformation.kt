package com.codebusters.idealizeprojectdraft.Util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.squareup.picasso.Transformation

class CustomMaskTransformation(private val context: Context, private val maskId: Int) : Transformation {

    companion object {
        private val maskingPaint = Paint().apply {xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
    }

    override fun transform(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        // Create a new bitmap with the desired dimensions and fill it with the grey background color.
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Draw the mask onto the bitmap using the DST_IN mode.
        val mask = getMaskDrawable(context, maskId)
        mask.setBounds(0, 0, width, height)
        mask.draw(canvas)

        // Draw the image onto the bitmap using the SRC_IN mode.
        canvas.drawBitmap(source, 0f, 0f, maskingPaint)

        source.recycle()

        return result
    }

    override fun key(): String {
        return "MaskTransformation(maskId=${context.resources.getResourceEntryName(maskId)})"
    }

    private fun getMaskDrawable(context: Context, maskId: Int): Drawable {
        val drawable = ContextCompat.getDrawable(context, maskId)
            ?: throw IllegalArgumentException("maskId is invalid")

        return drawable
    }
}