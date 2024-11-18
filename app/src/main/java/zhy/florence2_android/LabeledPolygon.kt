package zhy.florence2_android

import com.google.gson.annotations.SerializedName

// LabeledPolygon class
data class LabeledPolygon (
    @SerializedName("label")
    var label: String? = null,
    @SerializedName("polygon")
    var polygon: List<Coordinates<Float>> = ArrayList(),
    @SerializedName("bBoxes")
    val bBoxes: MutableList<BoundingBox<Float>> = ArrayList(),
)
