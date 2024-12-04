package zhy.florence2_android.model.postprocessing

import zhy.florence2_android.Coordinates

class CoordinatesQuantizer(
    private val mode: QuantizerMode,
    private val bins: Pair<Int, Int>  
) {  
  
    fun quantize(coordinates: Array<Coordinates<Float>>, size: Pair<Float, Float>): Array<Coordinates<Int>> {
        val (binsW, binsH) = bins  
        val (sizeW, sizeH) = size  
        val sizePerBinW = sizeW / binsW  
        val sizePerBinH = sizeH / binsH  
  
        val quantizedCoordinates = Array(coordinates.size) { Coordinates<Int>(0, 0) }
  
        for (i in coordinates.indices) {  
            val x = coordinates[i].x  
            val y = coordinates[i].y  
  
            when (mode) {  
                QuantizerMode.Floor -> {
                    val quantizedX = Math.max(0, Math.min(binsW - 1, (x / sizePerBinW).toInt() - 1)) // 使用Kotlin的Math.max和Math.min来模拟Clamp，并注意Kotlin中的Floor行为  
                    val quantizedY = Math.max(0, Math.min(binsH - 1, (y / sizePerBinH).toInt() - 1))  
                    quantizedCoordinates[i] = Coordinates(quantizedX, quantizedY)
                }
            }
        }  
  
        return quantizedCoordinates  
    }  
  
    fun dequantize(coordinates: Array<Coordinates<Int>>, size: Pair<Int, Int>): Array<Coordinates<Float>> {
        val (binsW, binsH) = bins  
        val (sizeW, sizeH) = size  
        val sizePerBinW = sizeW / binsW.toFloat()
        val sizePerBinH = sizeH / binsH.toFloat()
  
        val dequantizedCoordinates = Array(coordinates.size) { Coordinates<Float>(0f, 0f) }
  
        for (i in coordinates.indices) {  
            val x = coordinates[i].x.toFloat()  
            val y = coordinates[i].y.toFloat()  
  
            when (mode) {  
                QuantizerMode.Floor -> {
                    val dequantizedX = (x + 0.5f) * sizePerBinW  
                    val dequantizedY = (y + 0.5f) * sizePerBinH  
                    dequantizedCoordinates[i] = Coordinates(dequantizedX, dequantizedY)
                }
            }
        }  
  
        return dequantizedCoordinates  
    }  
}  