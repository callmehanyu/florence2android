package zhy.florence2_android.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtLoggingLevel
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtSession.RunOptions
import android.content.Context
import android.util.Log
import zhy.florence2_android.FlorenceResults
import zhy.florence2_android.TaskTypes
import zhy.florence2_android.R
import zhy.florence2_android.TaskPromptsWithInputDict
import zhy.florence2_android.TaskPromptsWithoutInputsDict
import zhy.florence2_android.helper.TensorExtension
import zhy.florence2_android.model.postprocessing.ByteLevelDecoder
import zhy.florence2_android.model.postprocessing.Florence2PostProcessor
import zhy.florence2_android.model.postprocessing.GenerationConfig
import zhy.florence2_android.model.postprocessing.NormalizedConfig
import zhy.florence2_android.model.tokenizer.Florence2Tokenizer
import java.nio.FloatBuffer
import java.nio.LongBuffer

private const val TAG = "Florence2Model"


class Florence2Model(private val context: Context) {

    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()

    private val _sessionOptions: OrtSession.SessionOptions = OrtSession.SessionOptions().apply {
        setSessionLogLevel(OrtLoggingLevel.ORT_LOGGING_LEVEL_VERBOSE)
        setSessionLogVerbosityLevel(0)
//        registerCustomOpLibrary(OrtxPackage.getLibraryPath())
//        addConfigEntry("session.load_model_format", "ORT")
        addNnapi()

//        val po = mapOf<String, String>()
//        addXnnpack(po)
    }
    // decoder_model_merged_uint8.with_runtime_opt.ort
//    private val _sessionDecoderMerged: OrtSession = "decoder_model_merged_uint8.ort".createSDSession()
//    private val _sessionEmbedTokens: OrtSession = "embed_tokens_uint8.ort".createSDSession()
//    private val _sessionEncoder: OrtSession = "encoder_model_uint8.ort".createSDSession()
//    private val _sessionVisionEncoder: OrtSession = "vision_encoder_uint8.ort".createSDSession()

    private val _sessionDecoderMerged: OrtSession = R.raw.decoder_model_merged_uint8.createByteSession()
    private val _sessionEmbedTokens: OrtSession = R.raw.embed_tokens_uint8.createByteSession()
    private val _sessionEncoder: OrtSession = R.raw.encoder_model_uint8.createByteSession()
    private val _sessionVisionEncoder: OrtSession = R.raw.vision_encoder_uint8.createByteSession()
//    private val florence2ModelVisionEncoderTest = Florence2ModelVisionEncoderTest(context)
//    private val florence2ModelEncoderTest = Florence2ModelEncoderTest(context)

    private val _tokenizer: Florence2Tokenizer = Florence2Tokenizer.Init(context)
    private val _imageProcessor: CLIPImageProcessor = CLIPImageProcessor(context)
    private val _postProcessor: Florence2PostProcessor = Florence2PostProcessor()



    private fun Int.createByteSession(sessionOptions: OrtSession.SessionOptions = _sessionOptions): OrtSession {
        return ortEnv.createSession(context.resources.openRawResource(this).readBytes(), sessionOptions)
    }

    private fun ConstructPrompts(taskType: TaskTypes, textInput: String): String {
        return when (taskType) {
            in TaskPromptsWithoutInputsDict -> {
                TaskPromptsWithoutInputsDict[taskType] ?: ""
            }
            in TaskPromptsWithInputDict -> {
                String.format(TaskPromptsWithInputDict[taskType] ?: "", textInput)
            }
            else -> {
                throw Exception("not found task type$taskType")
            }
        }
    }

    fun Run(task: TaskTypes, imgPath: String, textInput: String): List<FlorenceResults> {

        val runOptions = RunOptions()

        val prompts = listOf<String>(ConstructPrompts(task, textInput))

        val (inputIdsForEncoder, attentionMaskForEncoder) = GetTextInputs(prompts)
        Log.d(TAG, "Run GetTextInputs")
        var (pixelValues, imgSize)                        = _imageProcessor.Preprocess(imgPath) // todo 不太准
//        var (pixelValues, imgSize)                        = _imageProcessor.PreprocessMock3(imgPath, "image_tensor.txt")
        Log.d(TAG, "Run _imageProcessor.Preprocess")
        val text_features = _sessionEmbedTokens.run(mapOf("input_ids" to inputIdsForEncoder), setOf("inputs_embeds"), runOptions)
        val inputsEmbeds  = text_features[0] as OnnxTensor
        pixelValues = TensorExtension.JoinBatches(listOf(pixelValues))
        val imageFeaturesResult = _sessionVisionEncoder.run(mapOf("pixel_values" to pixelValues), setOf("image_features"), runOptions)
        val imageFeatures       = imageFeaturesResult[0] as OnnxTensor
//        val imageFeatures = mockImageFeature(context)
//        val imageFeatures = florence2ModelVisionEncoderTest.run(pixelValues)
        Log.d(TAG, "Run imageFeatures")
        val (inputsEmbedsMerged, attentionMaskMerged) = MergeInputIdsWithImageFeatures(inputsEmbeds, imageFeatures, attentionMaskForEncoder)
        Log.d(TAG, "Run MergeInputIdsWithImageFeatures")
        val forwardOut = _sessionEncoder.run(mapOf("attention_mask" to attentionMaskMerged, "inputs_embeds" to inputsEmbedsMerged), setOf( "last_hidden_state" ), runOptions)
//        val forwardOut = florence2ModelEncoderTest.run(attentionMaskMerged, inputsEmbedsMerged)
        Log.d(TAG, "Run forwardOut")

        val lastHiddenState = forwardOut?.get(0) as OnnxTensor

        val encoderOutputs = lastHiddenState
//        val encoderOutputs = mockEncoderOutputs(context)// todo

        val result = GenerationLoop(attentionMaskMerged, encoderOutputs, runOptions)
        Log.d(TAG, "Run GenerationLoop")
        return result.map { r ->
            Log.d(TAG, "Run PostProcessGeneration")
            _postProcessor.PostProcessGeneration(r, task, imgSize)
        }
    }

    private fun GetTextInputs(sentences: List<String>): Pair<OnnxTensor, OnnxTensor> {
        val numSentences = sentences.size

        // 假设encode方法返回List<EncodedSentence>，其中EncodedSentence是一个包含InputIds和AttentionMask的数据类
        val encoded = _tokenizer.Encode(sentences.toList())

        // 假设每个编码后的句子都有相同长度的token
        val tokenCount = encoded.first().first.size

        // 计算所有InputIds和AttentionMask的总长度
        val totalInputIdsLength = encoded.sumBy { it.first.size }
        val totalAttentionMaskLength = encoded.sumBy { it.second.size }

        // 初始化数组来存储所有InputIds和AttentionMask
        val inputIds = LongArray(totalInputIdsLength)
        val attentionMask = LongArray(totalAttentionMaskLength)

        // 用于追踪当前数组位置的索引
        var currentInputIdIndex = 0
        var currentAttentionMaskIndex = 0

        // 遍历编码后的句子，将InputIds和AttentionMask复制到对应的数组中
        encoded.forEach { encodedSentence ->
            encodedSentence.first.forEachIndexed { index, value ->
                inputIds[currentInputIdIndex + index] = value
            }
            currentInputIdIndex += encodedSentence.first.size

            encodedSentence.second.forEachIndexed { index, value ->
                attentionMask[currentAttentionMaskIndex + index] = value
            }
            currentAttentionMaskIndex += encodedSentence.second.size
        }

        // 创建维度数组
        val dimensions = longArrayOf(numSentences.toLong(), tokenCount.toLong())

        // 创建并返回DenseTensor对象
        val inputIdsBuf = LongBuffer.allocate(inputIds.size).apply {
            put(inputIds)
            rewind()
        }
        val inputIdsTensor = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), inputIdsBuf, dimensions)
        val attentionMaskBuf = LongBuffer.allocate(attentionMask.size).apply {
            put(attentionMask)
            rewind()
        }
        val attentionMaskTensor = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), attentionMaskBuf, dimensions)
        return Pair(inputIdsTensor, attentionMaskTensor)
    }

    private fun MergeInputIdsWithImageFeatures(
        inputsEmbeds: OnnxTensor,
        imageFeatures: OnnxTensor,
        attentionMask: OnnxTensor,
    ): Pair<OnnxTensor, OnnxTensor> {
        val inputs_embeds = TensorExtension.ConcatTensor(
            imageFeatures, // image embeds
            inputsEmbeds, // task prefix embeds
            axis = 1)
        val attentionMask = TensorExtension.ConcatenateAxis1Long(
            TensorExtension.OnesLong(imageFeatures.info.shape.sliceArray(0 until 2)), // image attention mask
            attentionMask, // task prefix attention mask
            )

        return inputs_embeds to attentionMask
    }

    private fun GenerationLoop(
        attentionMask: OnnxTensor,
        encoder_outputs: OnnxTensor,
        runOptions: RunOptions,
    ): List<String> {
        val batchSize = 1
        val batchIndex = 0
        val maxLength = GenerationConfig.MaxLength
        val numBeams = GenerationConfig.NumBeams
        val topK = GenerationConfig.TopK

        val noRepeatNgramSize: Int = GenerationConfig.NoRepeatNgramSize

        val decoderStartTokenID = _tokenizer.TokenToID(_tokenizer.Tokens.EndOfSequence)
        var decoderInputIds = TensorExtension.OnesLong(listOf<Long>(batchSize.toLong(), 1).toLongArray(), decoderStartTokenID.toLong())
        val allInputIds = Array(batchSize) {
            mutableListOf(decoderStartTokenID.toLong())
        }

        val results = mutableListOf<String>()

        var pastKeyValues: Map<String, OnnxTensor>? = null

        val logitsProcessors = mutableListOf<LogitsProcessor>()

        logitsProcessors.add(NoRepeatNGramLogitsProcessor(noRepeatNgramSize))
        logitsProcessors.add(ForcedBOSTokenLogitsProcessor(_tokenizer.TokenToID(_tokenizer.Tokens.BeginningOfSequence)))
        logitsProcessors.add(
            ForcedEOSTokenLogitsProcessor(
                maxLength,
                _tokenizer.TokenToID(_tokenizer.Tokens.EndOfSequence)
            )
        )
        val sampler = BeamSearchSampler(TensorOperationRegistry.TopKSession(_sessionOptions), topK, numBeams)
        val stoppingCriteria = mutableListOf<StoppingCriteria>()
        stoppingCriteria.add(MaxLengthCriteria(maxLength))
        stoppingCriteria.add(EosTokenCriteria(longArrayOf(_tokenizer.TokenToID(_tokenizer.Tokens.EndOfSequence).toLong())));

        val decoder = ByteLevelDecoder(_tokenizer.AddedTokens)

        val scores = DoubleArray(batchSize)

        var zhyCnt = 0

        while (true) {
            Log.d(TAG, "while (true) start zhyCnt=$zhyCnt")
            zhyCnt ++

            if (zhyCnt > 100) {
                break
            }

            val decoderInputsEmbeds = _sessionEmbedTokens.run(mapOf("input_ids" to decoderInputIds), setOf("inputs_embeds"), runOptions) // inputIds -> input_embeds
            val useCacheBranche = pastKeyValues != null
            val useCacheBranch = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), BooleanArray(1) { useCacheBranche })

            val decoderInputsEmbedsVec = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), decoderInputsEmbeds[0].value as Array<Array<FloatArray>>)
            val decoderFeeds = mutableMapOf<String, OnnxTensor>(
                "inputs_embeds" to          decoderInputsEmbedsVec,
                "encoder_attention_mask" to attentionMask,
                "encoder_hidden_states" to  encoder_outputs,
                "use_cache_branch" to       useCacheBranch,
            )

            if (pastKeyValues == null) {
                pastKeyValues = InitPastKeyValues(NormalizedConfig())
            }

            if (pastKeyValues != null) {
                decoderFeeds.putAll(pastKeyValues)
            }

            val decoder_out = _sessionDecoderMerged.run(
                decoderFeeds,
                setOf(
                    "logits",
                    "present.0.decoder.key",
                    "present.0.decoder.value",
                    "present.0.encoder.key",
                    "present.0.encoder.value",
                    "present.1.decoder.key",
                    "present.1.decoder.value",
                    "present.1.encoder.key",
                    "present.1.encoder.value",
                    "present.2.decoder.key",
                    "present.2.decoder.value",
                    "present.2.encoder.key",
                    "present.2.encoder.value",
                    "present.3.decoder.key",
                    "present.3.decoder.value",
                    "present.3.encoder.key",
                    "present.3.encoder.value",
                    "present.4.decoder.key",
                    "present.4.decoder.value",
                    "present.4.encoder.key",
                    "present.4.encoder.value",
                    "present.5.decoder.key",
                    "present.5.decoder.value",
                    "present.5.encoder.key",
                    "present.5.encoder.value"
                ),
                runOptions
            )
            pastKeyValues = FromPresent(decoder_out, useCacheBranche, pastKeyValues)
            val logits = decoder_out["logits"]
            val logitsValue = logits.get().value as Array<Array<FloatArray>>
//            val logitsTensor = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), logitsValue)
            val logitsTensorProcessedValue = logitsValue.map { it[0] }.toTypedArray()
            var logitsTensorProcessed = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), logitsTensorProcessedValue)
            logitsProcessors.forEach { logitsProcessor ->
                logitsTensorProcessed = logitsProcessor.Process(batchIndex, allInputIds[batchIndex].toLongArray(), logitsTensorProcessed)
            }
            val sampledTokens = sampler.sample(batchIndex, logitsTensorProcessed)
            val generatedInputIds: Array<List<Long>> = Array<List<Long>>(batchSize) { mutableListOf() }

            Log.d(TAG, "GenerationLoop generatedInputIds init")

            for ((token, score) in sampledTokens) {
                scores[batchIndex] += score
                val batchAllInputIds = allInputIds[batchIndex]
                batchAllInputIds.add(token)
                allInputIds[batchIndex] = batchAllInputIds

                val batchgeneratedInputIds = generatedInputIds[batchIndex].toMutableList()
                batchgeneratedInputIds.add(token)
                generatedInputIds[batchIndex] = batchgeneratedInputIds
                // TODO: Support beam search or just remove this
                Log.d(TAG, "GenerationLoop sampledTokens loop end")

                break
            }

            Log.d(TAG, "GenerationLoop isDone init")
            val isDone = BooleanArray(batchSize)
            stoppingCriteria.forEach { stoppingCriterion ->
                Log.d(TAG, "GenerationLoop stoppingCriteria.forEach start")
                val criterionDone = stoppingCriterion.call(allInputIds.map { it.toList() }.toTypedArray(), scores)
                for (i in isDone.indices) {
                    isDone[i] = isDone[i] || criterionDone[i]
                }
                Log.d(TAG, "GenerationLoop stoppingCriteria.forEach end")
            }

            if (isDone.all { it }) {
                results.addAll(allInputIds.map { allInputId ->
                    Log.d(TAG, "GenerationLoop DecodeSingle start")
                    DecodeSingle(_tokenizer, decoder, allInputId.map { it.toInt() }.toIntArray())
                })
                // 在Kotlin中，'break' 和 'continue' 只能在循环中使用，如果这段代码是在循环中，
                // 那么'break'可以保留；否则，它应该被移除或替换为其他逻辑。
                // 假设这段代码是在一个循环中，我们保留'break'：
                break
            } else {
                Log.d(TAG, "GenerationLoop decoderInputIds start")
                // 假设generatedInputIds是List<List<Long>>类型，我们需要将其展平为Long数组
                val generatedInputIdsLongArray = generatedInputIds.flatMap { it }.toLongArray()
                val generatedInputIdsLongBuffer = LongBuffer.allocate(generatedInputIdsLongArray.size).apply {
                    put(generatedInputIdsLongArray)
                    rewind()
                }
                decoderInputIds = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), generatedInputIdsLongBuffer, longArrayOf(generatedInputIds.size.toLong(), 1))
                // 注意：DenseTensor的构造函数和参数可能需要根据实际的库或框架进行调整
            }
        }
        return results
    }

    private fun FromPresent(decoderOut: OrtSession.Result, useCache: Boolean, pastKeyValues: Map<String, OnnxTensor>): Map<String, OnnxTensor> {
        val result = mutableMapOf<String, OnnxTensor>()
        decoderOut.forEach { decoderOutput ->
            if (decoderOutput.key.startsWith("present")) {
                val newName = decoderOutput.key.replace("present", "past_key_values")
                if (useCache && decoderOutput.key.contains("encoder")) {
                    //use cache
                    val vec = pastKeyValues[newName]?.value as Array<Array<Array<FloatArray>>>
                    result[newName] = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), vec)
                } else {
                    val vec = decoderOutput.value.value as Array<Array<Array<FloatArray>>>
                    result[newName] = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), vec)
                }
            }
        }
        return result
    }

    fun InitPastKeyValues(normalizedConfig: NormalizedConfig): Map<String, OnnxTensor> {
        val prefix = "past_key_values"
        val batchSize = 1L

        val encoderDimKv = normalizedConfig.EncoderHiddenSize / normalizedConfig.NumEncoderHeads
        val decoderDimKv = normalizedConfig.DecoderHiddenSize / normalizedConfig.NumDecoderHeads

        val encoderDims = longArrayOf(batchSize, normalizedConfig.NumDecoderHeads, 0, encoderDimKv.toLong())
        val decoderDims = longArrayOf(batchSize, normalizedConfig.NumDecoderHeads, 0, decoderDimKv)

        val pastKeyValues = mutableMapOf<String, OnnxTensor>()

        for (i in 0 until normalizedConfig.NumDecoderLayers) {
            pastKeyValues["${prefix}.${i}.encoder.key"] = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), FloatBuffer.allocate(0), encoderDims)
            pastKeyValues["${prefix}.${i}.encoder.value"] = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), FloatBuffer.allocate(0), encoderDims)
            pastKeyValues["${prefix}.${i}.decoder.key"] = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), FloatBuffer.allocate(0), decoderDims)
            pastKeyValues["${prefix}.${i}.decoder.value"] = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), FloatBuffer.allocate(0), decoderDims)
        }

        return pastKeyValues
    }

    companion object {

        private fun DecodeSingle(tokenizer: Florence2Tokenizer, decoder: ByteLevelDecoder, tokenIds: IntArray): String {
            // 将tokenIds转换为对应的token
            val tokens = tokenIds.map { tokenizer.IdToToken(it) }

            // 解码token链
            var decoded = decoder.decodeChain(tokenizer, tokens).joinToString("")

            // 清理解码结果
            decoded = CleanUpTokenization(decoded)

            return decoded
        }

        private fun CleanUpTokenization(text: String): String {
            // Clean up a list of simple English tokenization artifacts
            // like spaces before punctuations and abbreviated forms
            return text.replace(" .", ".")
                .replace(" ?", "?")
                .replace(" !", "!")
                .replace(" ,", ",")
                .replace(" ' ", "")
                .replace(" n't", "n't")
                .replace(" 'm", "'m")
                .replace(" 's", "'s")
                .replace(" 've", "'ve")
                .replace(" 're", "'re")
        }
    }

}