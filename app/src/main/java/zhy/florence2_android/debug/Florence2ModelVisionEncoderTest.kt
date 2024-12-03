package zhy.florence2_android.debug

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtLoggingLevel
import ai.onnxruntime.OrtSession
import ai.onnxruntime.providers.NNAPIFlags
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import zhy.florence2_android.R
import zhy.florence2_android.TaskTypes
import zhy.florence2_android.model.CLIPImageProcessor
import java.util.EnumSet

private const val TEST = "Florence2ModelVisionEncoderTest"



fun runOcrTaskVisionEncoderTest(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2ModelVisionEncoderTest(context)

        val task = TaskTypes.OCR
        val resultsBook = modelSession.run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

class Florence2ModelVisionEncoderTest(private val context: Context) {

    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()

    private val _sessionOptions: OrtSession.SessionOptions = OrtSession.SessionOptions().apply {
        setSessionLogLevel(OrtLoggingLevel.ORT_LOGGING_LEVEL_VERBOSE)
        setSessionLogVerbosityLevel(0)
//        registerCustomOpLibrary(OrtxPackage.getLibraryPath())
//        addConfigEntry("session.load_model_format", "ORT")
        addNnapi(EnumSet.of(NNAPIFlags.USE_FP16))

//        val po = mapOf<String, String>()
//        addXnnpack(po)
    }
    val runOptions = OrtSession.RunOptions()


    private val _imageProcessor: CLIPImageProcessor = CLIPImageProcessor(context)


    private val _sessionVisionEncoder: OrtSession = R.raw.vision_encoder_uint8.createByteSession()
    private val _sessionVisionEncoder_part1: OrtSession = R.raw.vision_encoder_part1.createByteSession()
    private val _sessionVisionEncoder_part2: OrtSession = R.raw.vision_encoder_part2.createByteSession()
    private val _sessionVisionEncoder_part3: OrtSession = R.raw.vision_encoder_part3.createByteSession()
    private val _sessionVisionEncoder_part4: OrtSession = R.raw.vision_encoder_part4.createByteSession()
    private val _sessionVisionEncoder_part5: OrtSession = R.raw.vision_encoder_part5.createByteSession()
    private val _sessionVisionEncoder_part6: OrtSession = R.raw.vision_encoder_part6.createByteSession()
    private val _sessionVisionEncoder_part7: OrtSession = R.raw.vision_encoder_part7.createByteSession()
    private val _sessionVisionEncoder_part8: OrtSession = R.raw.vision_encoder_part8.createByteSession()
    private val _sessionVisionEncoder_part9: OrtSession = R.raw.vision_encoder_part9.createByteSession()
    private val _sessionVisionEncoder_part10: OrtSession = R.raw.vision_encoder_part10.createByteSession()
    private val _sessionVisionEncoder_part11: OrtSession = R.raw.vision_encoder_part11.createByteSession()
    private val _sessionVisionEncoder_part12: OrtSession = R.raw.vision_encoder_part12.createByteSession()

    private fun Int.createByteSession(sessionOptions: OrtSession.SessionOptions = _sessionOptions): OrtSession {
        return ortEnv.createSession(
            context.resources.openRawResource(this).readBytes(),
            sessionOptions
        )
    }

    fun runPart1(task: TaskTypes, imgPath: String, textInput: String) {
        var (pixelValues, imgSize) = _imageProcessor.PreprocessMock4(
            imgPath,
            "image_tensor.txt"
        )
        val imageFeaturesResult = _sessionVisionEncoder_part1.run(
            mapOf("pixel_values" to pixelValues),
            setOf("/convs.1/Transpose_output_0"),
            runOptions
        )

        Log.d(TEST, "runPart1 imageFeaturesResult=$imageFeaturesResult")

    }

    fun run(task: TaskTypes, imgPath: String, textInput: String): OnnxTensor {
        val (pixelValues, imgSize) = _imageProcessor.PreprocessMock4(
            imgPath,
            "image_tensor.txt"
        )

        val _sessionVisionEncoder_part12_result = run(pixelValues)

        Log.d(TEST, "runPart1Section1 imageFeaturesResult=$_sessionVisionEncoder_part12_result")

        return _sessionVisionEncoder_part12_result

    }

    fun run(pixelValues: OnnxTensor): OnnxTensor {
        val _sessionVisionEncoder_part1_result = _sessionVisionEncoder_part1.run(
            mapOf("pixel_values" to pixelValues),
            setOf("/convs.1/Transpose_output_0"),
            runOptions
        )

        val _sessionVisionEncoder_part2_result = _sessionVisionEncoder_part2.run(
            mapOf("/convs.1/Transpose_output_0" to _sessionVisionEncoder_part1_result["/convs.1/Transpose_output_0"].get() as OnnxTensor),
            setOf("/convs.2/Transpose_output_0"),
            runOptions
        )

        val _sessionVisionEncoder_part3_result = _sessionVisionEncoder_part3.run(
            mapOf("/convs.2/Transpose_output_0" to _sessionVisionEncoder_part2_result["/convs.2/Transpose_output_0"].get() as OnnxTensor),
            setOf(
                "/blocks.2/blocks.2.0/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.1/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part4_result = _sessionVisionEncoder_part4.run(
            mapOf(
                "/blocks.2/blocks.2.0/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part3_result["/blocks.2/blocks.2.0/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.1/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part3_result["/blocks.2/blocks.2.1/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.1/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.2/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part5_result = _sessionVisionEncoder_part5.run(
            mapOf(
                "/blocks.2/blocks.2.1/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part4_result["/blocks.2/blocks.2.1/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.2/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part4_result["/blocks.2/blocks.2.2/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.2/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.3/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part6_result = _sessionVisionEncoder_part6.run(
            mapOf(
                "/blocks.2/blocks.2.2/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part5_result["/blocks.2/blocks.2.2/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.3/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part5_result["/blocks.2/blocks.2.3/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.3/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.4/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part7_result = _sessionVisionEncoder_part7.run(
            mapOf(
                "/blocks.2/blocks.2.3/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part6_result["/blocks.2/blocks.2.3/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.4/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part6_result["/blocks.2/blocks.2.4/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.4/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.5/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part8_result = _sessionVisionEncoder_part8.run(
            mapOf(
                "/blocks.2/blocks.2.4/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part7_result["/blocks.2/blocks.2.4/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.5/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part7_result["/blocks.2/blocks.2.5/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.5/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.6/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part9_result = _sessionVisionEncoder_part9.run(
            mapOf(
                "/blocks.2/blocks.2.5/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part8_result["/blocks.2/blocks.2.5/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.6/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part8_result["/blocks.2/blocks.2.6/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.6/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.7/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part10_result = _sessionVisionEncoder_part10.run(
            mapOf(
                "/blocks.2/blocks.2.6/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part9_result["/blocks.2/blocks.2.6/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.7/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part9_result["/blocks.2/blocks.2.7/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/blocks.2/blocks.2.7/channel_block/ffn/Add_output_0",
                "/blocks.2/blocks.2.8/spatial_block/conv1/fn/Reshape_output_0"
            ),
            runOptions
        )
        val _sessionVisionEncoder_part11_result = _sessionVisionEncoder_part11.run(
            mapOf(
                "/blocks.2/blocks.2.7/channel_block/ffn/Add_output_0" to _sessionVisionEncoder_part10_result["/blocks.2/blocks.2.7/channel_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.2/blocks.2.8/spatial_block/conv1/fn/Reshape_output_0" to _sessionVisionEncoder_part10_result["/blocks.2/blocks.2.8/spatial_block/conv1/fn/Reshape_output_0"].get() as OnnxTensor,
            ),
            setOf("/convs.3/Transpose_output_0"),
            runOptions
        )
        val _sessionVisionEncoder_part12_result = _sessionVisionEncoder_part12.run(
            mapOf(
                "pixel_values" to pixelValues,
                "/convs.3/Transpose_output_0" to _sessionVisionEncoder_part11_result["/convs.3/Transpose_output_0"].get() as OnnxTensor,
            ),
            setOf("image_features"),
            runOptions
        )
        return _sessionVisionEncoder_part12_result[0] as OnnxTensor
    }


}
