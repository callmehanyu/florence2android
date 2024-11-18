package zhy.florence2_android.model.postprocessing

enum class PostProcessingTypes {
    od,
    ocr_with_region,
    pure_text,
    description_with_polygons,
    description_with_bboxes,
    phrase_grounding,
    polygons,
    description_with_bboxes_or_polygons,
    bboxes
}