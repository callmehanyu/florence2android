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

fun Bitmap.drawRedRectanglesWithCoordinates(rects: List<Rect>): Bitmap {
    // 创建一个可变的 Bitmap 副本
    val mutableBitmap = copy(Bitmap.Config.ARGB_8888, true)

    // 创建画布并关联到 Bitmap
    val canvas = Canvas(mutableBitmap)

    // 创建画笔用于绘制矩形
    val rectPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f  // 设置边框宽度
    }

    // 创建画笔用于绘制文本
    val textPaint = Paint().apply {
        color = Color.RED
        textSize = 20f  // 设置文本大小
    }

    // 绘制每个矩形和坐标
    for (rect in rects) {
        // 绘制矩形
        canvas.drawRect(rect, rectPaint)

        // 绘制坐标文本
        val coordinates = listOf(
            "(${rect.left}, ${rect.top})",
            "(${rect.right}, ${rect.top})",
            "(${rect.left}, ${rect.bottom})",
            "(${rect.right}, ${rect.bottom})"
        )

        // 绘制每个角的坐标
        canvas.drawText(coordinates[0], rect.left.toFloat(), rect.top.toFloat(), textPaint)
        canvas.drawText(coordinates[1], rect.right.toFloat(), rect.top.toFloat(), textPaint)
        canvas.drawText(coordinates[2], rect.left.toFloat(), rect.bottom.toFloat(), textPaint)
        canvas.drawText(coordinates[3], rect.right.toFloat(), rect.bottom.toFloat(), textPaint)
    }

    return mutableBitmap
}
