package zhy.florence2_android.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF

//将bitmap调整到指定大小
fun Bitmap.sizeBitmap(newWidth: Int, newHeight: Int): Bitmap {
    val height = getHeight()
    val width = getWidth()
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight) // 使用后乘
    val newBM = Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
    if (!isRecycled) { //这时候origin还有吗？
        recycle()
    }
    return newBM
}

//按比例缩放
fun scaleBitmap(origin: Bitmap?, scale: Float): Bitmap? {
    if (origin == null) {
        return null
    }
    val width = origin.getWidth()
    val height = origin.getHeight()
    val matrix = Matrix()
    matrix.preScale(scale, scale)
    val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
    if (newBM == origin) {
        return newBM
    }
    origin.recycle()
    return newBM
}

fun cropBitmap(bitmap: Bitmap): Bitmap { //从中间截取一个正方形
    val w = bitmap.getWidth() // 得到图片的宽，高
    val h = bitmap.getHeight()
    val cropWidth = if (w >= h) h else w // 裁切后所取的正方形区域边长
    return Bitmap.createBitmap(
        bitmap, (bitmap.getWidth() - cropWidth) / 2,
        (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth
    )
}

fun getCircleBitmap(bitmap: Bitmap?): Bitmap? { //把图片裁剪成圆形
    var bitmap = bitmap ?: return null
    bitmap = cropBitmap(bitmap) //裁剪成正方形
    return try {
        val circleBitmap = Bitmap.createBitmap(
            bitmap.getWidth(),
            bitmap.getHeight(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(circleBitmap)
        val paint = Paint()
        val rect = Rect(
            0, 0, bitmap.getWidth(),
            bitmap.getHeight()
        )
        val rectF = RectF(
            Rect(
                0, 0, bitmap.getWidth(),
                bitmap.getHeight()
            )
        )
        var roundPx = 0.0f
        roundPx = bitmap.getWidth().toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(Color.WHITE)
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        val src = Rect(
            0, 0, bitmap.getWidth(),
            bitmap.getHeight()
        )
        canvas.drawBitmap(bitmap, src, rect, paint)
        circleBitmap
    } catch (e: Exception) {
        bitmap
    }
}

fun Bitmap.crop(rect: Rect): Bitmap {
    val cropedWidth = width.coerceAtMost(rect.width())
    val cropedHeight = height.coerceAtMost(rect.height())
    return Bitmap.createBitmap(this, rect.left, rect.top, cropedWidth, cropedHeight)
}

fun Bitmap.scaleBitmap(
    newWidth: Int,
    newHeight: Int
): Bitmap {
    val cropedWidth = width.coerceAtMost(newWidth)
    val cropedHeight = height.coerceAtMost(newHeight)
    return Bitmap.createScaledBitmap(this, cropedWidth, cropedHeight, true)
}
