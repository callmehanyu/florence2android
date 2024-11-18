package zhy.florence2_android.model.postprocessing

import zhy.florence2_android.BoundingBox
import kotlin.math.floor

class BoxQuantizer(
    private val mode: QuantizerMode,
    private val bins: Pair<Int, Int>  
) {  
  
    fun quantize(boxes: Array<BoundingBox<Float>>, size: Pair<Int, Int>): Array<BoundingBox<Int>> {
        val (binsW, binsH) = bins  
        val (sizeW, sizeH) = size  
        val sizePerBinW = sizeW.toFloat() / binsW  
        val sizePerBinH = sizeH.toFloat() / binsH  
  
        val quantizedBoxes = Array(boxes.size) { BoundingBox<Int>(0, 0, 0, 0) } // 初始化数组
  
        for (i in boxes.indices) {  
            when (mode) {  
                QuantizerMode.Floor -> {
                    quantizedBoxes[i] = BoundingBox(
                        xmin = Math.max(0, Math.min(binsW - 1, floor(boxes[i].xmin / sizePerBinW).toInt())),
                        ymin = Math.max(0, Math.min(binsH - 1, floor(boxes[i].ymin / sizePerBinH).toInt())),
                        xmax = Math.max(0, Math.min(binsW - 1, floor(boxes[i].xmax / sizePerBinW).toInt())),
                        ymax = Math.max(0, Math.min(binsH - 1, floor(boxes[i].ymax / sizePerBinH).toInt()))
                    )  
                }  
            }
        }  
  
        return quantizedBoxes  
    }  
  
    fun dequantize(boxes: Array<BoundingBox<Int>>, size: Pair<Int, Int>): Array<BoundingBox<Float>> {
        val (binsW, binsH) = bins  
        val (sizeW, sizeH) = size  
        val sizePerBinW = sizeW.toFloat() / binsW  
        val sizePerBinH = sizeH.toFloat() / binsH  
  
        val dequantizedBoxes = Array(boxes.size) { BoundingBox<Float>(0f, 0f, 0f, 0f) } // 初始化数组
  
        for (i in boxes.indices) {  
            when (mode) {  
                QuantizerMode.Floor -> {
                    dequantizedBoxes[i] = BoundingBox(
                        xmin = (boxes[i].xmin + 0.5f) * sizePerBinW,  
                        ymin = (boxes[i].ymin + 0.5f) * sizePerBinH,  
                        xmax = (boxes[i].xmax + 0.5f) * sizePerBinW,  
                        ymax = (boxes[i].ymax + 0.5f) * sizePerBinH  
                    )  
                }  
            }
        }  
  
        return dequantizedBoxes  
    }  
}

