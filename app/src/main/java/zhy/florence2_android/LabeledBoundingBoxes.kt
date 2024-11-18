package zhy.florence2_android

import com.google.gson.annotations.SerializedName

// LabeledBoundingBoxes class
data class LabeledBoundingBoxes(
    @SerializedName("bBoxes")
    var bBoxes: Array<BoundingBox<Float>> = arrayOf(),
    @SerializedName("label")
    var label: String? = null,
)