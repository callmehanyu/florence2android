package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor

interface ILogitsSampler {
      
    // Sample方法，返回Pair<Long, Double>的Iterable  
    fun sample(batchIdx: Int, logits: OnnxTensor): Map<Long, Double>
}  