package zhy.florence2_android

import com.google.gson.annotations.SerializedName

// LabeledOCRBox class
data class LabeledOCRBox(
    @SerializedName("quadBox")
    var quadBox: Array<Coordinates<Float>>,
    @SerializedName("text")
    var text: String? = null,
)
