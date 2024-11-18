package zhy.florence2_android

import com.google.gson.annotations.SerializedName

data class FlorenceResults(
    @SerializedName("OCRBBox")
    var OCRBBox: Array<LabeledOCRBox>? = null,

    @SerializedName("PureText")
    var PureText: String? = null,

    @SerializedName("BoundingBoxes")
    var BoundingBoxes: Array<LabeledBoundingBoxes>? = null,

    @SerializedName("Polygons")
    var Polygons: Array<LabeledPolygon>? = null,
)
