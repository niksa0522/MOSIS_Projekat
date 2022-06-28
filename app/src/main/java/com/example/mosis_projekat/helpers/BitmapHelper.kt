package com.example.mosis_projekat.helpers

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.example.mosis_projekat.R

object BitmapHelper {
    fun dp(value: Float,context: Context): Int {
        return if (value == 0f) {
            0
        } else Math.ceil((context.getResources().getDisplayMetrics().density * value).toDouble()).toInt()
    }
    fun createBitmap(bitmap: Bitmap, context: Context): Bitmap?{
        var result: Bitmap? = null
        try {
            result = Bitmap.createBitmap(dp(62f,context), dp(76f,context), Bitmap.Config.ARGB_8888)
            result.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(result)
            val drawable: Drawable = context.getResources().getDrawable(R.drawable.livepin)
            drawable.setBounds(0, 0, dp(62f,context), dp(76f,context))
            drawable.draw(canvas)
            val roundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val bitmapRect = RectF()
            canvas.save()
            //val bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar)
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                val matrix = Matrix()
                val scale: Float = dp(52f,context) / bitmap.width.toFloat()
                matrix.postTranslate(dp(0f,context).toFloat(), dp(0f,context).toFloat())
                matrix.postScale(scale, scale)
                roundPaint.setShader(shader)
                shader.setLocalMatrix(matrix)
                bitmapRect[dp(5f,context).toFloat(), dp(5f,context).toFloat(), dp(52f + 5f,context).toFloat()] = dp(52f + 5f,context).toFloat()
                canvas.drawRoundRect(bitmapRect, dp(26f,context).toFloat(), dp(26f,context).toFloat(), roundPaint)
            }
            canvas.restore()
            try {
                canvas.setBitmap(null)
            } catch (e: Exception) {
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result
    }
}