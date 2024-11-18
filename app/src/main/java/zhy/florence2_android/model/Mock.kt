package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import android.content.Context
import zhy.florence2_android.helper.readAssetFile

fun mockImageFeature(context: Context): OnnxTensor {

    val inputString = readAssetFile(context, "image_feature.txt")

    // 解析字符串为整数列表
    val numbers = inputString
        .split(",")
        .map { it.toFloat() }

    // 定义三维数组的维度
    val shape = Triple(1, 577, 768)

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
    val input_normalized = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), tensorData)
    return input_normalized

}

fun mockEncoderOutputs(context: Context): OnnxTensor {

    val inputString = readAssetFile(context, "encoderOutputs.txt")

    // 解析字符串为整数列表
    val numbers = inputString
        .split(",")
        .map { it.toFloat() }

    // 定义三维数组的维度
    val shape = Triple(1, 577+10, 768)

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
    val input_normalized = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), tensorData)
    return input_normalized

}
