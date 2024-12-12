package zhy.florence2_android

import android.graphics.Rect
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

fun BoundingBox<Float>.mapToRect() = Rect(xmin.toInt(), ymin.toInt(), xmax.toInt(), ymax.toInt())