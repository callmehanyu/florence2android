package zhy.florence2_android.media

import android.content.Context
import android.provider.MediaStore
import android.util.Log

private const val TAG = "LocalMediaRepo"


class LocalMediaRepo(private val context: Context) {

    fun getAllImage() {
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val mContentResolver = context.contentResolver
        val projImage = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        // 只查询jpeg和png的图片 //"image/mp4","image/3gp"
        val cursor = mContentResolver.query(
            imageUri,
            projImage,
            MediaStore.Images.Media.MIME_TYPE + " in(?, ?, ?)",
            arrayOf("image/jpeg", "image/png", "image/jpg"),
            MediaStore.Images.Media.DATE_MODIFIED + " desc"
        ) ?: return
        val pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        if (cursor.moveToFirst()) {
            do {    // 获取图片的路径
                val path = cursor.getString(pathIndex)
                // 获取该图片的父路径名
//                val parentFile = File(path).parentFile ?: continue
                //获取的文件地址
//                val dirPath = parentFile.absolutePath
                Log.d(TAG, "path $path")
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

}