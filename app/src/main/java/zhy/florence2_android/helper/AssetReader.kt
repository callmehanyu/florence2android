package zhy.florence2_android.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


fun String.getBitmapFromAsset(context: Context): Bitmap {
    val inputStream = context.assets.open(this)
     return BitmapFactory.decodeStream(inputStream)
}

fun readAssetFile(context: Context, fileName: String): String {
    val assetManager = context.assets
    val stringBuilder = StringBuilder()
    try {
        assetManager.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

fun processLargeAssetFileByDelimiter(context: Context, fileName: String, delimiter: Char = ',') {
    val assetManager = context.assets

    try {
        assetManager.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val stringBuilder = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                    stringBuilder.append("\n")

                    var content = stringBuilder.toString()
                    var index: Int

                    while (content.indexOf(delimiter).also { index = it } != -1) {
                        val part = content.substring(0, index)
                        processPart(part)
                        content = content.substring(index + 1)
                    }

                    stringBuilder.setLength(0)
                    stringBuilder.append(content)
                }

                // 处理最后剩余的部分
                if (stringBuilder.isNotEmpty()) {
                    processPart(stringBuilder.toString())
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun processPart(part: String) {
    // 处理每个部分的逻辑
    println(part.trim())
}