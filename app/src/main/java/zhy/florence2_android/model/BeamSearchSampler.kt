package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession

class BeamSearchSampler(
    private val topKSession: OrtSession,
    private val top_k: Int,
    private val num_beams: Int,

) : ILogitsSampler {
    override fun sample(batchIdx: Int, logits: OnnxTensor): Map<Long, Double> {
        var k = logits.info.shape[logits.info.shape.size - 1] // defaults to vocab size

        if (this.top_k > 0) {
            k = Math.min(this.top_k.toLong(), k)
        }

        val result = TensorOperationRegistry.CallTopK(
            topKSession,
            logits,
            OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), longArrayOf(k))
        )
        val v      = (result.get("v").get().value as Array<FloatArray>)[0]
        val i      = (result.get("i").get().value as Array<LongArray>)[0]

        // Compute softmax over logits
        val probabilities = Softmax(v)

        val scoreResult = HashMap<Long, Double>()
        for (x in 0 until num_beams) {
            scoreResult[i[x]] = Math.log(probabilities[x].toDouble())
        }
        return scoreResult
    }

    companion object {
        fun Softmax(arr: FloatArray): FloatArray {
            // Compute the maximum value in the array
            val maxVal = arr.maxOrNull() ?: throw NoSuchElementException("Array is empty")

            // Compute the exponentials of the array values
            val exps = arr.map { Math.exp(it - maxVal.toDouble()).toFloat() }.toFloatArray()

            // Compute the sum of the exponentials
            val sumExps = exps.sum()

            // Compute the softmax values
            val softmaxArr = exps.map { it / sumExps }.toFloatArray()

            return softmaxArr
        }
    }
}