package com.pet.frompet.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import com.pet.frompet.R


class MapMakerBorder(private val context: Context, private val borderWidth: Float) : Transformation {
    override fun key(): String = "gradient-border-$borderWidth"

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val output = pool.get(input.width, input.height, input.config)
        val canvas = Canvas(output)
        val redColor = ContextCompat.getColor(context, R.color.red)
        val pinkColor = ContextCompat.getColor(context, R.color.orange)

        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            isAntiAlias = true
            shader = RadialGradient(
                input.width / 2f, input.height / 2f, input.width / 2f,
                intArrayOf(redColor,pinkColor),
                null, Shader.TileMode.CLAMP
            )
        }
        val rect = RectF(0f + borderWidth / 2, 0f + borderWidth / 2, input.width - borderWidth / 2, input.height - borderWidth / 2)
        canvas.drawBitmap(input, 0f, 0f, null)
        canvas.drawOval(rect, paint)
        return output
    }
}
