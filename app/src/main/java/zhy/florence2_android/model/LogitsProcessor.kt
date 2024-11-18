package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor

interface LogitsProcessor {

    fun Process(batchID: Int, input_ids: LongArray, logits: OnnxTensor): OnnxTensor?

    companion object {

        /**
         * @return
         */
        fun GetBatchSlice(batchID: Int, tensor: OnnxTensor): List<Float> {
            return if (tensor.info.shape.size == 2) {
                tensor.floatBuffer.array().sliceArray((batchID * tensor.info.shape[1]).toInt() until  tensor.info.shape[1].toInt()).toList()
            } else if (tensor.info.shape.size == 3 && tensor.info.shape[1] == 1L) {
                tensor.floatBuffer.array().sliceArray((batchID * tensor.info.shape[2]).toInt() until tensor.info.shape[2].toInt()).toList()
            } else {
                TODO()
            }

        }
    }

}