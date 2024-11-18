package zhy.florence2_android.model.postprocessing

enum class QuantizerMode {
    Floor,
}
data class ParseTask(
    var TASK_NAME: PostProcessingTypes,
    var AREA_THRESHOLD: Double = 0.0, // 默认值  
    var FILTER_BY_BLACK_LIST: Boolean = false // 默认值  
)  
  
class Florence2PostProcessorConfig {  
    var NUM_BBOX_HEIGHT_BINS: Int = 1000  
    var NUM_BBOX_WIDTH_BINS: Int = 1000  
    var BOX_QUANTIZATION_MODE: QuantizerMode = QuantizerMode.Floor
    var COORDINATES_HEIGHT_BINS: Int = 1000  
    var COORDINATES_WIDTH_BINS: Int = 1000  
    var COORDINATES_QUANTIZATION_MODE: QuantizerMode = QuantizerMode.Floor
    var PARSE_TASKS: List<ParseTask> = listOf(
        ParseTask(PostProcessingTypes.od),
        ParseTask(PostProcessingTypes.ocr_with_region, 0.01),
        ParseTask(PostProcessingTypes.phrase_grounding, 0.0, true), // 注意这里的默认值0.0和true，因为Kotlin不允许在构造函数中直接跳过参数
        ParseTask(PostProcessingTypes.pure_text),
        ParseTask(PostProcessingTypes.description_with_bboxes),
        ParseTask(PostProcessingTypes.description_with_polygons),
        ParseTask(PostProcessingTypes.polygons),
        ParseTask(PostProcessingTypes.bboxes),
        ParseTask(PostProcessingTypes.description_with_bboxes_or_polygons)
    )  
}