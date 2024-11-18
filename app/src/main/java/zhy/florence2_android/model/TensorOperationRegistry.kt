package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession

object TensorOperationRegistry {

    fun TopKSession(sessionOptions: OrtSession.SessionOptions): OrtSession {
        //see tensorflow.js
        val sessionBytes = byteArrayOf(8, 10, 18, 0, 58, 73, 10, 18, 10, 1, 120, 10, 1, 107, 18, 1, 118, 18, 1, 105, 34, 4, 84, 111, 112, 75, 18, 1, 116, 90, 9, 10, 1, 120, 18, 4, 10, 2, 8, 1, 90, 15, 10, 1, 107, 18, 10, 10, 8, 8, 7, 18, 4, 10, 2, 8, 1, 98, 9, 10, 1, 118, 18, 4, 10, 2, 8, 1, 98, 9, 10, 1, 105, 18, 4, 10, 2, 8, 7, 66, 2, 16, 21)
        return OrtEnvironment.getEnvironment().createSession(sessionBytes, sessionOptions)
    }

    fun CallTopK(session: OrtSession, x: OnnxTensor, k: OnnxTensor): OrtSession.Result {
        return session.run(mapOf("k" to k, "x" to x))
    }
}