package zhy.florence2_android.model.postprocessing

import zhy.florence2_android.TaskTypes

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

fun GetPostProcessingType(taskType: TaskTypes): PostProcessingTypes {
    return when (taskType) {
        TaskTypes.OCR -> PostProcessingTypes.pure_text
        TaskTypes.OCR_WITH_REGION -> PostProcessingTypes.ocr_with_region
        TaskTypes.CAPTION -> PostProcessingTypes.pure_text
        TaskTypes.DETAILED_CAPTION -> PostProcessingTypes.pure_text
        TaskTypes.MORE_DETAILED_CAPTION -> PostProcessingTypes.pure_text
        TaskTypes.OD -> PostProcessingTypes.description_with_bboxes
        TaskTypes.DENSE_REGION_CAPTION -> PostProcessingTypes.description_with_bboxes
        TaskTypes.CAPTION_TO_PHRASE_GROUNDING -> PostProcessingTypes.phrase_grounding
        TaskTypes.REFERRING_EXPRESSION_SEGMENTATION -> PostProcessingTypes.polygons
        TaskTypes.REGION_TO_SEGMENTATION -> PostProcessingTypes.polygons
        TaskTypes.OPEN_VOCABULARY_DETECTION -> PostProcessingTypes.description_with_bboxes_or_polygons
        TaskTypes.REGION_TO_CATEGORY -> PostProcessingTypes.pure_text
        TaskTypes.REGION_TO_DESCRIPTION -> PostProcessingTypes.pure_text
        TaskTypes.REGION_TO_OCR -> PostProcessingTypes.pure_text
        TaskTypes.REGION_PROPOSAL -> PostProcessingTypes.bboxes
    }
}