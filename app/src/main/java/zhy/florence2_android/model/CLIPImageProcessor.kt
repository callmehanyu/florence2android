package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import zhy.florence2_android.helper.crop
import zhy.florence2_android.helper.getBitmapFromAsset
import zhy.florence2_android.helper.readAssetFile

private const val TAG = "CLIPImageProcessor"

class CLIPImageProcessor(private val context: Context) {

    fun Preprocess(imgPath: String): Pair<OnnxTensor, Pair<Int, Int>> {
        val CropHeight = 768
        val CropWidth = 768
        val bitmap = getBitmapFromAsset(context, imgPath)
            .crop(Rect(0, 0, CropWidth, CropHeight))
//            .scaleBitmap(CropWidth, CropHeight)
        val tensorData = Array(3) {
            Array(CropHeight) {
                FloatArray(CropWidth)
            }
        }

        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val standardDeviation = floatArrayOf(0.229f, 0.224f, 0.225f)
        val RescaleFactor  = 0.00392156862745098f


        for (y in 0 until CropHeight) {
            for (x in 0 until CropWidth) {
                val pixel = if (x < bitmap.width && y < bitmap.height) {
                    bitmap.getPixel(x, y)
                } else {
                    Color.argb(255, 1,2,4)
                }
                // Get RGB values
                val blue = pixel and 0xFF
                val green = (pixel shr 8) and 0xFF
                val red = (pixel shr 16) and 0xFF

                tensorData[0][y][x] = (blue * RescaleFactor - mean[0]) / standardDeviation[0]
                tensorData[1][y][x] = (green * RescaleFactor - mean[1]) / standardDeviation[1]
                tensorData[2][y][x] = (red * RescaleFactor - mean[2]) / standardDeviation[2]
//                Log.d(TAG, "Preprocess end")
            }
        }

        val input_normalized = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), tensorData)
        return input_normalized to (bitmap.width to bitmap.height)
    }

    fun PreprocessMock(imgPath: String, path: String): Pair<OnnxTensor, Pair<Int, Int>> {
        val CropHeight = 768
        val CropWidth = 768
        val bitmap = getBitmapFromAsset(context, imgPath)
            .crop(Rect(0, 0, CropWidth, CropHeight))

        val inputString = readAssetFile(context, path)

        // 解析字符串为整数列表
        val numbers = inputString
            .removeSurrounding("[", "]")
            .split(",")
            .map { it.toFloat() }

        // 定义三维数组的维度
        val shape = Triple(3, 768, 768)

        // 初始化三维数组
        val tensorData = Array(shape.first) {
            Array(shape.second) {
                FloatArray(shape.third)
            }
        }

        // 填充三维数组
        var index = 0
        for (i in 0 until shape.first) {
            for (j in 0 until shape.second) {
                for (k in 0 until shape.third) {
                    tensorData[i][j][k] = numbers[index % numbers.size]
                    index++
                }
            }
        }
//        val tensorDataInt = tensorData.map { // todo
//            it.map {
//                it.map {
//                    it.toInt()
//                }.toIntArray()
//            }.toTypedArray()
//        }.toTypedArray()
        val input_normalized = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), tensorData)
        return input_normalized to (bitmap.width to bitmap.height)

    }

}
