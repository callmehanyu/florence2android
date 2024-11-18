package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment


/**
 * A logits processor that disallows ngrams of a certain size to be repeated.
 */
class NoRepeatNGramLogitsProcessor(private val noRepeatNgramSize: Int) : LogitsProcessor {

    private fun GetNgrams(batchID: Int, prevInputIds: LongArray): Map<LongArray, LongArray>{
        val curLen = prevInputIds.size
        val ngrams = mutableListOf<LongArray>()

        for (j in 0 until curLen + 1 - this.noRepeatNgramSize) {
            val ngram = LongArray(noRepeatNgramSize)
            for (k in 0 until this.noRepeatNgramSize) {
                ngram[k] = prevInputIds[j + k]
            }
            ngrams.add(ngram)
        }

        val generatedNgram = HashMap<LongArray, LongArray>()

        ngrams.forEach { ngram ->
            // 获取前一个n-gram（去掉最后一个元素）
            val prevNgram = ngram.copyOfRange(0, ngram.size - 1)

            // 将prevNgram转换为不可变列表以用作键（因为Kotlin中的Array不是很好的Map键）
            val prevNgramKey = prevNgram.toList().toLongArray()

            // 获取与prevNgramKey关联的现有值（如果有的话），否则使用空列表
            val prevNgramValue = generatedNgram.getOrDefault(prevNgramKey, longArrayOf()).toMutableList()

            // 向prevNgramValue添加当前ngram的最后一个元素
            prevNgramValue.add(ngram.last())

            // 将更新后的列表放回字典中
            generatedNgram[prevNgramKey] = prevNgramValue.toLongArray()
        }
        return generatedNgram
    }

    /**
     * Generate n-grams from a sequence of token ids.
     * @param bannedNgrams Map of banned n-grams
     * @param prevInputIds List of previous input ids
     * @returns Map of generated n-grams
     */
    private fun GetGeneratedNgrams(batchID: Int, bannedNgrams: Map<LongArray, LongArray>, prevInputIds: LongArray): LongArray {
        // 计算ngramIdx的起始索引和结束索引
        val startIdx = prevInputIds.size + 1 - noRepeatNgramSize
        val endIdx = prevInputIds.size

        // 获取ngramIdx数组（在Kotlin中，使用sliceArray方法或copyOfRange进行数组切片）
        val ngramIdx = prevInputIds.sliceArray(startIdx until endIdx)

        // 将ngramIdx转换为不可变列表以用作字典的键（因为Kotlin中的Array不是Map的好键）
        val ngramIdxKey = ngramIdx.toList().toLongArray()

        // 获取与ngramIdxKey关联的禁止n-gram列表（如果存在的话），否则使用空列表
        val banned = bannedNgrams.getOrDefault(ngramIdxKey, longArrayOf())

        // 将禁止n-gram列表转换为LongArray并返回
        return banned
    }

    private fun CalcBannedNgramTokens(batchID: Int, prevInputIds: LongArray): LongArray {
        if (prevInputIds.size + 1 < this.noRepeatNgramSize) {
            // 如果没有生成 noRepeatNgramSize 个 token，则返回空数组
            return longArrayOf()
        } else {
            // 获取生成的 n-gram 字典
            val generatedNgrams = this.GetNgrams(batchID, prevInputIds)

            // 获取禁止的 n-gram token 数组
            val bannedTokens = this.GetGeneratedNgrams(batchID, generatedNgrams, prevInputIds)

            // 返回禁止的 token 数组
            return bannedTokens
        }
    }

    override fun Process(batchID: Int, input_ids: LongArray, logits: OnnxTensor): OnnxTensor? {
        val bannedTokens = CalcBannedNgramTokens(batchID, input_ids)
        val processedLogits = logits.value as Array<FloatArray>
        bannedTokens.forEach { token ->
//            logits[batchID, token] = float.NegativeInfinity;
            processedLogits[batchID][token.toInt()] = Float.NEGATIVE_INFINITY
        }
        return OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), processedLogits)
    }
}