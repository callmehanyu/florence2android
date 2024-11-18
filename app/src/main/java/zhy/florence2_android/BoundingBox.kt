package zhy.florence2_android

import com.google.gson.annotations.SerializedName

// BoundingBox class  
data class BoundingBox<T>(
    @SerializedName("xmin")
    val xmin: T,
    @SerializedName("ymin")
    val ymin: T,
    @SerializedName("xmax")
    val xmax: T,
    @SerializedName("ymax")
    val ymax: T,
    )