package zhy.florence2_android.model.postprocessing

import zhy.florence2_android.BoundingBox
import zhy.florence2_android.Coordinates
import zhy.florence2_android.FlorenceResults
import zhy.florence2_android.LabeledBoundingBoxes
import zhy.florence2_android.LabeledOCRBox
import zhy.florence2_android.LabeledPolygon
import zhy.florence2_android.TaskTypes
import java.util.regex.Pattern
import kotlin.math.abs


class Florence2PostProcessor(
    private val config: Florence2PostProcessorConfig = Florence2PostProcessorConfig(),
    private val parseTaskConfigs: Map<PostProcessingTypes, ParseTask> = mapOf(),
    private val boxQuantizer: BoxQuantizer = BoxQuantizer(config.BOX_QUANTIZATION_MODE, config.NUM_BBOX_WIDTH_BINS to config.NUM_BBOX_HEIGHT_BINS),
    private val coordinatesQuantizer: CoordinatesQuantizer = CoordinatesQuantizer(config.BOX_QUANTIZATION_MODE, config.COORDINATES_WIDTH_BINS to config.COORDINATES_HEIGHT_BINS),
    private val blackListOfPhraseGrounding: Set<String> = setOf(
        "it", "I", "me", "mine",
        "you", "your", "yours",
        "he", "him", "his",
        "she", "her", "hers",
        "they", "them", "their", "theirs",
        "one", "oneself",
        "we", "us", "our", "ours",
        "you", "your", "yours",
        "they", "them", "their", "theirs",
        "mine", "yours", "his", "hers", "its",
        "ours", "yours", "theirs",
        "myself", "yourself", "himself", "herself", "itself",
        "ourselves", "yourselves", "themselves",
        "this", "that",
        "these", "those",
        "who", "whom", "whose", "which", "what",
        "who", "whom", "whose", "which", "that",
        "all", "another", "any", "anybody", "anyone", "anything",
        "each", "everybody", "everyone", "everything",
        "few", "many", "nobody", "none", "one", "several",
        "some", "somebody", "someone", "something",
        "each other", "one another",
        "myself", "yourself", "himself", "herself", "itself",
        "ourselves", "yourselves", "themselves",
        "the image", "image", "images", "the", "a", "an", "a group",
        "other objects", "lots", "a set",
    ),
) {

    fun PostProcessGeneration(text: String, task: TaskTypes, imageSize: Pair<Int, Int>): FlorenceResults {
        val postPRocessingTask: PostProcessingTypes = GetPostProcessingType(task)
        when (postPRocessingTask) {
            PostProcessingTypes.pure_text -> {
                return FlorenceResults().apply {
                    PureText = ReplaceStartAndEndToken(text)
                }
            }
            PostProcessingTypes.ocr_with_region -> {
                val ocrs = ParseOcrFromTextAndSpans(text, imageSize, parseTaskConfigs[postPRocessingTask]?.AREA_THRESHOLD ?: 0.01)
                return FlorenceResults().apply{
                    OCRBBox = ocrs.toTypedArray()
                }
            }
            PostProcessingTypes.od, PostProcessingTypes.bboxes, PostProcessingTypes.description_with_bboxes -> {
                val boxes = ParseDescriptionWithBboxesFromTextAndSpans(text, imageSize = imageSize)
                return FlorenceResults().apply {
                    BoundingBoxes = boxes.toTypedArray()
                }
            }
            PostProcessingTypes.phrase_grounding -> {
                val bboxes = ParsePhraseGroundingFromTextAndSpans(text, imageSize)
                return  FlorenceResults().apply {
                    BoundingBoxes = bboxes.toList().toTypedArray()
                }
            }
            PostProcessingTypes.description_with_polygons -> {
                val polygons = ParseDescriptionWithPolygonsFromTextAndSpans(text, imageSize)
                return  FlorenceResults().apply {
                    Polygons = polygons.toList().toTypedArray()
                }
            }
            PostProcessingTypes.polygons -> {
                val polygons = ParseDescriptionWithPolygonsFromTextAndSpans(text, imageSize, true)
                return  FlorenceResults().apply {
                    Polygons = polygons.toList().toTypedArray()
                }
            }
            PostProcessingTypes.description_with_bboxes_or_polygons -> {
                return if (text.contains("<poly>")) {
                    val polygons = ParseDescriptionWithPolygonsFromTextAndSpans(text, imageSize)
                    FlorenceResults().apply {
                        Polygons = polygons.toList().toTypedArray()
                    }
                } else {
                    val bboxes = ParseDescriptionWithBboxesFromTextAndSpans(text, imageSize)
                    FlorenceResults().apply {
                        BoundingBoxes = bboxes.toTypedArray()
                    }
                }
            }
        }
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


    fun ReplaceStartAndEndToken(text: String): String {
        return text.replace("<s>", "").replace("</s>", "")
    }

    fun ParseOcrFromTextAndSpans(
        text: String,
        imageSize: Pair<Int, Int>,
        areaThreshold: Double = -1.0
    ): List<LabeledOCRBox> {
        val pattern = "(.+?)<loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)>".toRegex()
        val instances = mutableListOf<LabeledOCRBox>()
        val cleanedText = text.replace("<s>", "")

        val parsed = pattern.findAll(cleanedText)
        val imageWidth = imageSize.first
        val imageHeight = imageSize.second

        for (ocrLine in parsed) {
            val ocrContent = ocrLine.groups[1]?.value ?: ""
            val quadBox = mutableListOf<Int>()

            for (i in 2..9) {
                quadBox.add(ocrLine.groups[i]?.value?.toIntOrNull() ?: 0)
            }

            val dequantizedQuadBox = coordinatesQuantizer.dequantize(
                quadBox.chunk(2).map { it.toTypedArray() }.map { Coordinates(it[0], it[1]) }.toTypedArray(),
                imageSize
            )

            if (areaThreshold > 0) {
                val xCoords = mutableListOf<Float>()
                val yCoords = mutableListOf<Float>()

                for (t in dequantizedQuadBox) {
                    xCoords.add(t.x)
                    yCoords.add(t.y)
                }

                // Apply the Shoelace formula
                val area = 0.5 * abs((0 until 4).sumByDouble { i ->
                    (xCoords[i] * yCoords[(i + 1) % 4] - xCoords[(i + 1) % 4] * yCoords[i]).toDouble()
                })

                if (area < (imageWidth * imageHeight) * areaThreshold) {
                    continue
                }
            }

            instances.add(LabeledOCRBox(dequantizedQuadBox, ReplaceStartAndEndToken(ocrContent)))
        }

        return instances
    }

    fun ParseDescriptionWithBboxesFromTextAndSpans(
        text: String,
        imageSize: Pair<Int, Int>,
        allowEmptyPhrase: Boolean = false
    ): List<LabeledBoundingBoxes> {
        val patternQustionMark = "([a-zA-Z0-9 ]+)<loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)>".toRegex()

        // Ignore <s> </s> and <pad>
        val cleanedText = text.replace("<s>", "").replace("</s>", "").replace("<pad>", "")

        val phrasePattern = if (allowEmptyPhrase) {
            "(?:(?:<loc_\\d+>){4,})".toRegex()
        } else {
            "([^<]+(?:<loc_\\d+>){4,})".toRegex()
        }

        val phrases = phrasePattern.findAll(cleanedText).toList()

        val textPattern = "^\\s*(.*?)(?=<od>|</od>|<box>|</box>|<bbox>|</bbox>|<loc_)".toRegex()
        val boxPattern = "<loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)>".toRegex()

        val result = mutableListOf<LabeledBoundingBoxes>()

        for (phraseText in phrases) {
            val box = LabeledBoundingBoxes()

            val phraseTextStrip = phraseText.value
                .replace("<ground>", "")
                .replace("<obj>", "")

            if (phraseTextStrip.isNullOrEmpty() && !allowEmptyPhrase) {
                continue
            }

            // Parse phrase, get string
            val phraseMatch = textPattern.find(phraseTextStrip)

            if (phraseMatch == null) {
                continue
            }

            val phrase = phraseMatch.groups[1]?.value?.trim() ?: continue

            // Parse bboxes by box_pattern
            val bboxesParsed = boxPattern.findAll(phraseText.value).toList()

            if (bboxesParsed.isEmpty()) {
                continue
            }

            val bboxBins = bboxesParsed.map { match ->
                val coords = (1..4).map { i -> match.groups[i]?.value?.toIntOrNull() ?: 0 }
                BoundingBox(coords[0], coords[1],coords[2],coords[3])
            }.toTypedArray()

            // Assuming boxQuantizer is defined somewhere and has a Dequantize method
             box.bBoxes = boxQuantizer.dequantize(bboxBins, imageSize) // Replace with actual dequantization logic

            // Exclude non-ASCII characters
            val cleanedPhrase = phrase.replace("[^\\u0000-\\u007F]".toRegex(), "")

            // Assuming ReplaceStartAndEndToken is defined somewhere
            // box.label = replaceStartAndEndToken(cleanedPhrase) // Replace with actual token replacement logic
            box.label = ReplaceStartAndEndToken(cleanedPhrase) // Modified directly for simplicity in this example

            // Assuming Dequantize and ReplaceStartAndEndToken methods are defined elsewhere
            // and are used to process bboxBins and phrase respectively.
            // Here we're just adding a placeholder for the dequantized bboxes.
//            box.bBoxes = arrayOf() // Placeholder for dequantized bboxes

            result.add(box)
        }

        return result
    }

    fun ParsePhraseGroundingFromTextAndSpans(text: String, imageSize: Pair<Int, Int>): Sequence<LabeledBoundingBoxes> {
        // Ignore <s> </s> and <pad>
        val cleanedText = text.replace("<s>", "").replace("</s>", "").replace("<pad>", "")

        val phrasePattern = "([^<]+(?:<loc_\\d+>){4,})".toRegex()
        val phrases = phrasePattern.findAll(cleanedText)

        val textPattern = "^\\s*(.*?)(?=<od>|</od>|<box>|</box>|<bbox>|</bbox>|<loc_)".toRegex()
        val boxPattern = "<loc_(\\d+)><loc_(\\d+)><loc_(\\d+)><loc_(\\d+)>".toRegex()

        return phrases.asSequence().mapNotNull { phraseMatch ->
            val box = LabeledBoundingBoxes(arrayOf())

            val phraseText = phraseMatch.value
            val phraseTextStrip = phraseText.replace("<ground>", "", ignoreCase = true).replace("<obj>", "", ignoreCase = true)

            if (phraseTextStrip.isNullOrBlank()) {
                return@mapNotNull null
            }

            val phraseMatch1 = textPattern.find(phraseTextStrip) ?: return@mapNotNull null

            val bboxesParsed = boxPattern.findAll(phraseText)

            if (bboxesParsed.count() == 0) {
                return@mapNotNull null
            }

            val phrase = phraseMatch1.groups[1]?.value?.trim() ?: return@mapNotNull null

            if (blackListOfPhraseGrounding.contains(phrase)) {
                return@mapNotNull null
            }

            val bboxBins = bboxesParsed.map { match ->
                val coords = (1..4).map { i -> match.groups[i]?.value?.toIntOrNull() ?: 0 }.toTypedArray()
                BoundingBox(coords[0], coords[1],coords[2],coords[3])
            }.toList().toTypedArray()

            box.bBoxes = boxQuantizer.dequantize(bboxBins, imageSize)

            // Exclude non-ASCII characters
            val filteredPhrase = phrase.filter { it.toInt() < 128 }

            box.label = ReplaceStartAndEndToken(filteredPhrase)
            box
        }
    }

    fun ParseDescriptionWithPolygonsFromTextAndSpans(
        text: String,
        imageSize: Pair<Int, Int>,
        allowEmptyPhrase: Boolean = false,
        polygonSepToken: String = "<sep>",
        polygonStartToken: String = "<poly>",
        polygonEndToken: String = "</poly>",
        withBoxAtStart: Boolean = false
    ): Sequence<LabeledPolygon> {

        // Ignore <s> </s> and <pad>
        val cleanedText = text.replace("<s>", "").replace("</s>", "").replace("<pad>", "")

        val phrasePattern = if (allowEmptyPhrase) {
            """(?:(?:<loc_\d+>|${Pattern.quote(polygonSepToken)}|${Pattern.quote(polygonStartToken)}|${Pattern.quote(polygonEndToken)}){4,})"""
        } else {
            """([^<]+(?:<loc_\d+>|${Pattern.quote(polygonSepToken)}|${Pattern.quote(polygonStartToken)}|${Pattern.quote(polygonEndToken)}){4,})"""
        }

        val phrases = Regex(phrasePattern).findAll(cleanedText)

        val phraseStringPattern = """^\s*(.*?)(?=<od>|</od>|<box>|</box>|<bbox>|</bbox>|<loc_|<poly>)"""
        val boxPattern = """((?:<loc_\d+>)+)(?:${Pattern.quote(polygonSepToken)}|$)"""
        val polygonsInstancePattern = """${Pattern.quote(polygonStartToken)}(.*?)${Pattern.quote(polygonEndToken)}"""

        return phrases.asSequence().map { phraseText ->
            val box = LabeledPolygon()

            val phraseTextStrip = phraseText.value.replace("""^loc_\d+>""", "")

            if (phraseTextStrip.isNullOrEmpty() && !allowEmptyPhrase) {
                return@map null
            }

            val phraseMatch = Regex(phraseStringPattern).find(phraseTextStrip) ?: return@map null

            val phrase = phraseMatch.groups[1]?.value?.trim() ?: return@map null

            val polygonsInstancesParsed = if (phraseText.value.contains(polygonStartToken) && phraseText.value.contains(polygonEndToken)) {
                Regex(polygonsInstancePattern).findAll(phraseText.value).map { it.groups[1]?.value ?: "" }.toList()
            } else {
                listOf(phraseText.value)
            }

            polygonsInstancesParsed.forEachIndexed { index, polygonsInstance ->
                val polygonsParsed = Regex(boxPattern).findAll(polygonsInstance)

                if (polygonsParsed.count() == 0) return@forEachIndexed

                var bbox: BoundingBox<Int>? = null
                val fullPolygon = mutableListOf<Coordinates<Float>>()

                polygonsParsed.forEach { polygonParsed ->
                    var polygon = Regex("""<loc_(\d+)>""").findAll(polygonParsed.groups[1]?.value ?: "")
                        .map { it.groups[1]?.value?.toIntOrNull() ?: 0 }
                        .toMutableList()

                    if (withBoxAtStart && bbox == null) {
                        if (polygon.size > 4) {
                            bbox = BoundingBox(polygon[0], polygon[1],polygon[2],polygon[3],)
                            polygon.removeRange(0, 4)
                        } else {
                            bbox = BoundingBox(0, 0, 0, 0)
                        }
                    }

                    if (polygon.size % 2 == 1) { // abandon last element if is not paired
                        polygon = polygon.take(polygon.size - 1).toMutableList()
                    }

                    // Assuming coordinatesQuantizer and boxQuantizer are defined elsewhere
                    // and have corresponding dequantize methods.
                    val dequantizedPolygon = coordinatesQuantizer.dequantize(
                        polygon.chunked(2).map { Coordinates(it[0], it[1]) }.toTypedArray(),
                        imageSize
                    )

                    fullPolygon.addAll(dequantizedPolygon)

                    bbox?.let {
                        box.bBoxes.add(boxQuantizer.dequantize(arrayOf(it), imageSize)[0])
                    }

                }

                box.polygon = fullPolygon
                box.label = phrase
                return@map box
            }
            box
        }.filterNotNull()
    }
}

fun <T> List<T>.chunk(size: Int): List<List<T>> {
    require(size > 0) { "Chunk size must be greater than 0." }
    val result = mutableListOf<List<T>>()
    var index = 0

    while (index < this.size) {
        val end = (index + size).coerceAtMost(this.size)
        result.add(this.subList(index, end))
        index += size
    }

    return result
}

fun <T> MutableList<T>.removeRange(startIndex: Int, endIndex: Int) {
    this.subList(startIndex, endIndex).clear()
}