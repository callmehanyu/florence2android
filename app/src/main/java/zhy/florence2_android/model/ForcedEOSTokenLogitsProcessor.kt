package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment

class ForcedEOSTokenLogitsProcessor(private val max_length: Int, private val eos_token_id: Int):
    LogitsProcessor {
    override fun Process(batchID: Int, input_ids: LongArray, logits: OnnxTensor): OnnxTensor? {
        return if (input_ids.size == this.max_length - 1) {
            val sliceLogits = LogitsProcessor.GetBatchSlice(batchID, logits).toMutableList()
                .apply { replaceAll { Float.NEGATIVE_INFINITY } }.toFloatArray()
            val batchSliceLogits = Array(batchID+1) { sliceLogits }
            batchSliceLogits[batchID][eos_token_id] = 0f
            OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), batchSliceLogits)
        } else {
            logits
        }

    }
}