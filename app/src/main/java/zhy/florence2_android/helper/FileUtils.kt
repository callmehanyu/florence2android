package zhy.florence2_android.helper

import android.content.Context
import java.io.IOException


fun readAssetsFile(context: Context, fileName: String?): String {
    try {
        val `is` = context.assets.open(fileName!!)
        val fileLength = `is`.available()
        val buffer = ByteArray(fileLength)
        val readLength = `is`.read(buffer)
        `is`.close()
        return String(buffer, charset("utf-8"))
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return "读取错误，请检查文件是否存在"
}
