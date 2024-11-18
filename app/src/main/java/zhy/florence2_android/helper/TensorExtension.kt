package zhy.florence2_android.helper

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import android.util.Log
import java.nio.FloatBuffer
import java.nio.LongBuffer

private const val TAG = "TensorExtension"

object TensorExtension {

    fun JoinBatches(tensors: List<OnnxTensor>): OnnxTensor {
        if (tensors.size == 1) {
            val tensor = tensors[0]
            val dims = longArrayOf(1) + tensor.info.shape
            return OnnxTensor.createTensor(
                OrtEnvironment.getEnvironment(),
                tensor.floatBuffer,
                dims
            )
        }

        val dimensions = longArrayOf(tensors.size.toLong()) + tensors[0].info.shape

        val buffer = mutableListOf<Float>()

        tensors.forEach { tensor ->
            buffer.add(tensor.floatBuffer.get())
        }

        val tensor = FloatBuffer.allocate(buffer.size).apply {
            put(buffer.toFloatArray())
            rewind()
        }

        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), tensor, dimensions)
    }

    /// <summary>
    /// Concatenates the specified tensors along the specified axis.
    /// </summary>
    /// <param name="tensor1">The tensor1.</param>
    /// <param name="tensor2">The tensor2.</param>
    /// <param name="axis">The axis.</param>
    /// <returns></returns>
    /// <exception cref="System.NotImplementedException">Only axis 0,1,2 is supported</exception>
    public fun ConcatTensor(tensor1: OnnxTensor, tensor2: OnnxTensor, axis: Int = 0): OnnxTensor {
        if (tensor1.info.shape.size != tensor2.info.shape.size) {
            throw Exception("ConcatTensor tensor1.info.shape.size != tensor2.info.shape.size")
        }
        return when (axis) {
            0 -> ConcatenateAxis0(tensor1, tensor2)
            1 -> ConcatenateAxis1(tensor1, tensor2)
            2 -> ConcatenateAxis2(tensor1, tensor2)
            else -> TODO()
        }
    }


    private fun ConcatenateAxis0(tensor1: OnnxTensor, tensor2: OnnxTensor): OnnxTensor {
        val dimensions = tensor1.info.shape.copyOf()
        dimensions[0] += tensor2.info.shape[0]

        val data = tensor1.floatBuffer.array() + tensor2.floatBuffer.array()
        val dataBuf = FloatBuffer.allocate(data.size).apply {
            put(data)
            rewind()
        }
        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), dataBuf, dimensions)
    }

    private fun ConcatenateAxis1(tensor1: OnnxTensor, tensor2: OnnxTensor): OnnxTensor {
        val dimensions = tensor1.info.shape.copyOf()
        dimensions[1] += tensor2.info.shape[1]

        val concatenatedTensor = Array(dimensions[0].toInt()) {
            Array(dimensions[1].toInt()) {
                FloatArray(768)
            }
        }
        Log.d(TAG, "ConcatenateAxis1 init")

        // Copy data from the first tensor
        val dimensionsInt = dimensions.map { it.toInt() }
        val tensor1ShapeInt = tensor1.info.shape.map { it.toInt() }
        val tensor2ShapeInt = tensor2.info.shape.map { it.toInt() }

        val tensor1Value = tensor1.value as Array<Array<FloatArray>>
        val tensor2Value = tensor2.value as Array<Array<FloatArray>>

        for (i in 0 until dimensionsInt[0]) {
            for (j in 0 until tensor1ShapeInt[1]) {
                for (k in 0 until tensor1ShapeInt[2]) {
                    concatenatedTensor[i][j][k] = tensor1Value[i][j][k]
                }
            }
        }
        Log.d(TAG, "ConcatenateAxis1 1")

        // Copy data from the second tensor
        for (i in 0 until dimensionsInt[0]) {
            for (j in 0 until tensor2ShapeInt[1]) {
                for (k in 0 until tensor2ShapeInt[2]) {
                    concatenatedTensor[i][j + tensor1ShapeInt[1]][k] = tensor2Value[i][j][k]
                }
            }
        }
        Log.d(TAG, "ConcatenateAxis1 2")

//        val concatenatedTensorBuf = FloatBuffer.allocate(concatenatedTensor.size).apply {
//            put(concatenatedTensor)
//            rewind()
//        }
        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), concatenatedTensor)
    }

    private fun ConcatenateAxis2(tensor1: OnnxTensor, tensor2: OnnxTensor): OnnxTensor {
        val dimensions = tensor1.info.shape.copyOf()
        dimensions[2] += tensor2.info.shape[2]
        val concatenatedTensor = Array(dimensions[0].toInt()) {
            Array(dimensions[1].toInt()) {
                FloatArray(tensor1.info.shape[2].toInt())
            }
        }

        // Copy data from the first tensor
        for (i in 0 until dimensions[0]) {
            for (j in 0 until dimensions[1]) {
                for (k in 0 until tensor2.info.shape[2]) {
                    concatenatedTensor[i.toInt()][j.toInt()][k.toInt()] = (tensor2.value as Array<Array<FloatArray>>)[i.toInt()][j.toInt()][k.toInt()]
                }
            }
        }

        // Copy data from the second tensor
        for (i in 0 until dimensions[0]) {
            for (j in 0 until dimensions[1]) {
                for (k in 0 until tensor2.info.shape[2]) {
                    concatenatedTensor[i.toInt()][j.toInt()][(k+tensor1.info.shape[2]).toInt()] = (tensor2.value as Array<Array<FloatArray>>)[i.toInt()][j.toInt()][k.toInt()]
                }
            }
        }
        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), concatenatedTensor)
    }

    fun ConcatenateAxis1Long(tensor1: OnnxTensor, tensor2: OnnxTensor): OnnxTensor {
        val dimensions = tensor1.info.shape.copyOf()
        dimensions[1] += tensor2.info.shape[1]

        val concatenatedTensor = Array(dimensions[0].toInt()) {
            LongArray(dimensions[1].toInt())
        }
        Log.d(TAG, "ConcatenateAxis1Long init")

        // Copy data from the first tensor
        val dimensionsInt = dimensions.map { it.toInt() }
        val tensor1ShapeInt = tensor1.info.shape.map { it.toInt() }
        val tensor2ShapeInt = tensor2.info.shape.map { it.toInt() }

        val tensor1Value = tensor1.value as Array<LongArray>
        val tensor2Value = tensor2.value as Array<LongArray>

        for (i in 0 until dimensionsInt[0]) {
            for (j in 0 until tensor1ShapeInt[1]) {
                concatenatedTensor[i][j] = tensor1Value[i][j]
            }
        }
        Log.d(TAG, "ConcatenateAxis1Long 1")

        // Copy data from the second tensor
        for (i in 0 until dimensionsInt[0]) {
            for (j in 0 until tensor2ShapeInt[1]) {
                concatenatedTensor[i][j + tensor1ShapeInt[1]] = tensor2Value[i][j]
            }
        }
        Log.d(TAG, "ConcatenateAxis1Long 2")

        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), concatenatedTensor)
    }

    private fun Multiply(dimensions: LongArray): Long {
        var aggrgate = dimensions[0]
        for (i in 1 until dimensions.size) {
            aggrgate *= dimensions[i]
        }
        return aggrgate
    }

    fun OnesLong(dimensions: LongArray,  value: Long = 1): OnnxTensor
    {
        val resultSize = Multiply(dimensions)
        val arr        = LongArray(resultSize.toInt()) { value }
        for (i in 0 until resultSize) {
            arr[i.toInt()] = value
        }

        val result = LongBuffer.allocate(arr.size).apply {
            put(arr)
            rewind()
        }
        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), result, dimensions)
    }

}
