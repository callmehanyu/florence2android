package zhy.florence2_android

/**
 * todo 经过验证，每张图片都会有不同的任务无法执行
 */
enum class TaskTypes {
    OCR,
    OCR_WITH_REGION,
    CAPTION,
    DETAILED_CAPTION,
    MORE_DETAILED_CAPTION,
    OD,
    DENSE_REGION_CAPTION,
    CAPTION_TO_PHRASE_GROUNDING,
    REFERRING_EXPRESSION_SEGMENTATION,
    REGION_TO_SEGMENTATION,
    OPEN_VOCABULARY_DETECTION,
    REGION_TO_CATEGORY,
    REGION_TO_DESCRIPTION,
    REGION_TO_OCR,
    REGION_PROPOSAL
}

internal val TaskPromptsWithoutInputsDict: Map<TaskTypes, String> = mapOf(
    TaskTypes.OCR to "What is the text in the image?",
    TaskTypes.OCR_WITH_REGION to "What is the text in the image, with regions?",
    TaskTypes.CAPTION to "What does the image describe?",
    TaskTypes.DETAILED_CAPTION to "Describe in detail what is shown in the image.",
    TaskTypes.MORE_DETAILED_CAPTION to "Describe with a paragraph what is shown in the image.",
    TaskTypes.OD to "Locate the objects with category name in the image.",
    TaskTypes.DENSE_REGION_CAPTION to "Locate the objects in the image, with their descriptions.",
    TaskTypes.REGION_PROPOSAL to "Locate the region proposals in the image."
)

internal val TaskPromptsWithInputDict: Map<TaskTypes, String> = mapOf(
    TaskTypes.CAPTION_TO_PHRASE_GROUNDING to "Locate the phrases in the caption: %s",
    TaskTypes.REFERRING_EXPRESSION_SEGMENTATION to "Locate %s in the image with mask",
    TaskTypes.REGION_TO_SEGMENTATION to "What is the polygon mask of region %s",
    TaskTypes.OPEN_VOCABULARY_DETECTION to "Locate %s in the image.",
    TaskTypes.REGION_TO_CATEGORY to "What is the region %s?",
    TaskTypes.REGION_TO_DESCRIPTION to "What does the region %s describe?",
    TaskTypes.REGION_TO_OCR to "What text is in the region %s?"
)