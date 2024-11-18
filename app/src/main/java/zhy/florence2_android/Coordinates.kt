package zhy.florence2_android

import com.google.gson.annotations.SerializedName

// Coordinates class
data class Coordinates<T>(
    @SerializedName("x")
    val x: T,
    @SerializedName("y")
    val y: T,
)