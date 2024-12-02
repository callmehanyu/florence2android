package zhy.florence2_android.debug

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtLoggingLevel
import ai.onnxruntime.OrtSession
import android.content.Context
import zhy.florence2_android.R

private const val TEST = "Florence2ModelEncoderTest"

class Florence2ModelEncoderTest(private val context: Context) {

    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()

    private val _sessionOptions: OrtSession.SessionOptions = OrtSession.SessionOptions().apply {
        setSessionLogLevel(OrtLoggingLevel.ORT_LOGGING_LEVEL_VERBOSE)
        setSessionLogVerbosityLevel(0)
//        registerCustomOpLibrary(OrtxPackage.getLibraryPath())
//        addConfigEntry("session.load_model_format", "ORT")
//        addNnapi()

//        val po = mapOf<String, String>()
//        addXnnpack(po)
    }
    val runOptions = OrtSession.RunOptions()

    private val _sessionEncoder_part1: OrtSession = R.raw.encoder_model_part1.createByteSession()
    private val _sessionEncoder_part2: OrtSession = R.raw.encoder_model_part2.createByteSession()
    private val _sessionEncoder_part3: OrtSession = R.raw.encoder_model_part3.createByteSession()
    private val _sessionEncoder_part4: OrtSession = R.raw.encoder_model_part4.createByteSession()
    private val _sessionEncoder_part5: OrtSession = R.raw.encoder_model_part5.createByteSession()
    private val _sessionEncoder_part6: OrtSession = R.raw.encoder_model_part6.createByteSession()
    private val _sessionEncoder_part7: OrtSession = R.raw.encoder_model_part7.createByteSession()

    private val _sessionEncoder_part2_section1: OrtSession = R.raw.encoder_model_part2_section1.createByteSession()
    private val _sessionEncoder_part2_section2: OrtSession = R.raw.encoder_model_part2_section2.createByteSession()
    private val _sessionEncoder_part2_section3: OrtSession = R.raw.encoder_model_part2_section3.createByteSession()


    private fun Int.createByteSession(sessionOptions: OrtSession.SessionOptions = _sessionOptions): OrtSession {
        return ortEnv.createSession(
            context.resources.openRawResource(this).readBytes(),
            sessionOptions
        )
    }

    fun run(attention_mask: OnnxTensor, inputs_embeds: OnnxTensor, ): OrtSession.Result? {

        val _sessionEncoder_part1_result = _sessionEncoder_part1.run(
            mapOf("attention_mask" to attention_mask),
            setOf("/Where_1_output_0"),
            runOptions
        )

        // todo 不准
        val _sessionEncoder_part2_result = _sessionEncoder_part2.run(
            mapOf(
                "inputs_embeds" to inputs_embeds,
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
            ),
            setOf("/layers.0/final_layer_norm/Mul_output_0"),
            runOptions
        )

        val _sessionEncoder_part2_section1_result = _sessionEncoder_part2_section1.run(
            mapOf(
                "inputs_embeds" to inputs_embeds,
            ),
            setOf("/layernorm_embedding/Mul_output_0"),
            runOptions
        )

        // todo 不准
        val _sessionEncoder_part2_section2_result = _sessionEncoder_part2_section2.run(
            mapOf(
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
                "/layernorm_embedding/Mul_output_0" to _sessionEncoder_part2_section1_result["/layernorm_embedding/Mul_output_0"].get() as OnnxTensor,
            ),
            setOf("/layers.0/Add_output_0"),
            runOptions
        )

        val _sessionEncoder_part2_section3_result = _sessionEncoder_part2_section3.run(
            mapOf(
                "/layers.0/Add_output_0" to _sessionEncoder_part2_section2_result["/layers.0/Add_output_0"].get() as OnnxTensor,
            ),
            setOf("/layers.0/final_layer_norm/Mul_output_0"),
            runOptions
        )

        val _sessionEncoder_part3_result = _sessionEncoder_part3.run(
            mapOf(
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
                "/layers.0/final_layer_norm/Mul_output_0" to _sessionEncoder_part2_section3_result["/layers.0/final_layer_norm/Mul_output_0"].get() as OnnxTensor,
                ),
            setOf(
                "/layers.1/final_layer_norm/Mul_output_0",
            ),
            runOptions
        )
        val _sessionEncoder_part4_result = _sessionEncoder_part4.run(
            mapOf(
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
                "/layers.1/final_layer_norm/Mul_output_0" to _sessionEncoder_part3_result["/layers.1/final_layer_norm/Mul_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/layers.2/final_layer_norm/Mul_output_0",
            ),
            runOptions
        )
        val _sessionEncoder_part5_result = _sessionEncoder_part5.run(
            mapOf(
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
                "/layers.2/final_layer_norm/Mul_output_0" to _sessionEncoder_part4_result["/layers.2/final_layer_norm/Mul_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/layers.3/final_layer_norm/Mul_output_0",
            ),
            runOptions
        )
        val _sessionEncoder_part6_result = _sessionEncoder_part6.run(
            mapOf(
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
                "/layers.3/final_layer_norm/Mul_output_0" to _sessionEncoder_part5_result["/layers.3/final_layer_norm/Mul_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "/layers.4/final_layer_norm/Mul_output_0",
            ),
            runOptions
        )
        val _sessionEncoder_part7_result = _sessionEncoder_part7.run(
            mapOf(
                "/Where_1_output_0" to _sessionEncoder_part1_result["/Where_1_output_0"].get() as OnnxTensor,
                "/layers.4/final_layer_norm/Mul_output_0" to _sessionEncoder_part6_result["/layers.4/final_layer_norm/Mul_output_0"].get() as OnnxTensor,
            ),
            setOf(
                "last_hidden_state",
            ),
            runOptions
        )

        return _sessionEncoder_part7_result
    }


}
