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

private const val TEST = "Florence2ModelVisionEncoderUInt8Test"

/**
 *
 */
fun runOcrTaskVisionEncoderUInt8Test(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2ModelVisionEncoderUInt8Test(context)

        val task = TaskTypes.OCR
        val resultsBook = modelSession.runPart1Section1(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

class Florence2ModelVisionEncoderUInt8Test(private val context: Context) {

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
    private val _sessionVisionEncoder_part1: OrtSession = R.raw.vision_encoder_uint8_part1.createByteSession()
    private val _sessionVisionEncoder_part1_section1: OrtSession = R.raw.vision_encoder_uint8_part1_section1.createByteSession()
    private val _sessionVisionEncoder_part1_section2: OrtSession = R.raw.vision_encoder_uint8_part1_section2.createByteSession()
    private val _sessionVisionEncoder_part1_section3: OrtSession = R.raw.vision_encoder_uint8_part1_section3.createByteSession()
    private val _sessionVisionEncoder_part1_section4: OrtSession = R.raw.vision_encoder_uint8_part1_section4.createByteSession()
    private val _sessionVisionEncoder_part1_section5: OrtSession = R.raw.vision_encoder_uint8_part1_section5.createByteSession()
    private val _sessionVisionEncoder_part1_section6: OrtSession = R.raw.vision_encoder_uint8_part1_section6.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit1: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit1.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2_block1: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2_block1.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2_block2: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2_block2.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2_block1_segment1: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2_block1_segment1.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2_block1_segment2: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2_block1_segment2.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2_block1_segment3: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2_block1_segment3.createByteSession()
    private val _sessionVisionEncoder_part1_section6_unit2_block1_segment4: OrtSession = R.raw.vision_encoder_uint8_part1_section6_unit2_block1_segment4.createByteSession()

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

    fun runPart1Section1(task: TaskTypes, imgPath: String, textInput: String) {
        val (pixelValues, imgSize) = _imageProcessor.PreprocessMock4(
            imgPath,
            "image_tensor.txt"
        )
        val part1_section1Result = _sessionVisionEncoder_part1_section1.run(
            mapOf("pixel_values" to pixelValues),
            setOf("/convs.0/proj/Conv_output_0"),
            runOptions
        )

        //
        val part1_section2Result = _sessionVisionEncoder_part1_section2.run(
            mapOf("/convs.0/proj/Conv_output_0" to part1_section1Result["/convs.0/proj/Conv_output_0"].get() as OnnxTensor),
            setOf("/convs.0/norm/Add_1_output_0", "/blocks.0/blocks.0.0/spatial_block/conv1/fn/dw/Conv_output_0"),
            runOptions
        )

        val part1_section3Result = _sessionVisionEncoder_part1_section3.run(
            mapOf(
                "/convs.0/norm/Add_1_output_0" to part1_section2Result["/convs.0/norm/Add_1_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/spatial_block/conv1/fn/dw/Conv_output_0" to part1_section2Result["/blocks.0/blocks.0.0/spatial_block/conv1/fn/dw/Conv_output_0"].get() as OnnxTensor
            ),
            setOf("/blocks.0/blocks.0.0/spatial_block/conv2/fn/Reshape_output_0", "/blocks.0/blocks.0.0/spatial_block/window_attn/Add_output_0"),
            runOptions
        )

        val part1_section4Result = _sessionVisionEncoder_part1_section4.run(
            mapOf(
                "/blocks.0/blocks.0.0/spatial_block/conv2/fn/Reshape_output_0" to part1_section3Result["/blocks.0/blocks.0.0/spatial_block/conv2/fn/Reshape_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/spatial_block/window_attn/Add_output_0" to part1_section3Result["/blocks.0/blocks.0.0/spatial_block/window_attn/Add_output_0"].get() as OnnxTensor
            ),
            setOf("/blocks.0/blocks.0.0/spatial_block/ffn/Add_output_0", "/blocks.0/blocks.0.0/channel_block/conv1/fn/dw/Conv_output_0"),
            runOptions
        )

        val part1_section5Result = _sessionVisionEncoder_part1_section5.run(
            mapOf(
                "/blocks.0/blocks.0.0/spatial_block/ffn/Add_output_0" to part1_section4Result["/blocks.0/blocks.0.0/spatial_block/ffn/Add_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/channel_block/conv1/fn/dw/Conv_output_0" to part1_section4Result["/blocks.0/blocks.0.0/channel_block/conv1/fn/dw/Conv_output_0"].get() as OnnxTensor
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0", "/blocks.0/blocks.0.0/channel_block/conv2/fn/Reshape_output_0"),
            runOptions
        )

        // todo 不准
        val part1_section6Result = _sessionVisionEncoder_part1_section6.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0" to part1_section5Result["/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/channel_block/conv2/fn/Reshape_output_0" to part1_section5Result["/blocks.0/blocks.0.0/channel_block/conv2/fn/Reshape_output_0"].get() as OnnxTensor
            ),
            setOf("/convs.1/Transpose_output_0"),
            runOptions
        )

        val part1_section6_Unit1Result = _sessionVisionEncoder_part1_section6_unit1.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/conv2/fn/Reshape_output_0" to part1_section5Result["/blocks.0/blocks.0.0/channel_block/conv2/fn/Reshape_output_0"].get() as OnnxTensor
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0", "/convs.1/Unsqueeze_2_output_0", "/convs.1/Unsqueeze_1_output_0"),
            runOptions
        )

        // todo 不准
        val part1_section6_Unit2Result = _sessionVisionEncoder_part1_section6_unit2.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0" to part1_section5Result["/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0" to part1_section6_Unit1Result["/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0"].get() as OnnxTensor
            ),
            setOf("/convs.1/Unsqueeze_output_0", "/convs.1/norm/Add_1_output_0"),
            runOptions
        )

        // todo 不准
        val part1_section6_Unit2_block1Result = _sessionVisionEncoder_part1_section6_unit2_block1.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0" to part1_section5Result["/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0" to part1_section6_Unit1Result["/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0"].get() as OnnxTensor
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/conv2/Add_output_0", "/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc2/Add_output_0"),
            runOptions
        )

        val part1_section6_Unit2_block2Result = _sessionVisionEncoder_part1_section6_unit2_block2.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/conv2/Add_output_0" to part1_section6_Unit2_block1Result["/blocks.0/blocks.0.0/channel_block/conv2/Add_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc2/Add_output_0" to part1_section6_Unit2_block1Result["/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc2/Add_output_0"].get() as OnnxTensor
            ),
            setOf("/convs.1/Unsqueeze_output_0", "/convs.1/norm/Add_1_output_0"),
            runOptions
        )

        val part1_section6_Unit2_block1_segment1Result = _sessionVisionEncoder_part1_section6_unit2_block1_segment1.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0" to part1_section5Result["/blocks.0/blocks.0.0/channel_block/channel_attn/Add_output_0"].get() as OnnxTensor,
                "/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0" to part1_section6_Unit1Result["/blocks.0/blocks.0.0/channel_block/conv2/fn/Transpose_1_output_0"].get() as OnnxTensor
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/conv2/Add_output_0", "/blocks.0/blocks.0.0/channel_block/ffn/norm/Div_output_0"),
            runOptions
        )

        val part1_section6_Unit2_block1_segment2Result = _sessionVisionEncoder_part1_section6_unit2_block1_segment2.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/ffn/norm/Div_output_0" to part1_section6_Unit2_block1_segment1Result["/blocks.0/blocks.0.0/channel_block/ffn/norm/Div_output_0"].get() as OnnxTensor,
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc1/MatMul_output_0"),
            runOptions
        )

        val part1_section6_Unit2_block1_segment3Result = _sessionVisionEncoder_part1_section6_unit2_block1_segment3.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc1/MatMul_output_0" to part1_section6_Unit2_block1_segment2Result["/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc1/MatMul_output_0"].get() as OnnxTensor,
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/ffn/fn/net/act/Mul_1_output_0"),
            runOptions
        )

        val part1_section6_Unit2_block1_segment4Result = _sessionVisionEncoder_part1_section6_unit2_block1_segment4.run(
            mapOf(
                "/blocks.0/blocks.0.0/channel_block/ffn/fn/net/act/Mul_1_output_0" to part1_section6_Unit2_block1_segment3Result["/blocks.0/blocks.0.0/channel_block/ffn/fn/net/act/Mul_1_output_0"].get() as OnnxTensor,
            ),
            setOf("/blocks.0/blocks.0.0/channel_block/ffn/fn/net/fc2/Add_output_0"),
            runOptions
        )

        Log.d(TEST, "runPart1Section1 imageFeaturesResult=$part1_section6_Unit2_block1_segment4Result")

    }


}