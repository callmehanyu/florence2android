package zhy.florence2_android.media.knowledgegraph.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
    @PrimaryKey @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "ocr_text") val ocrText: String,
    @ColumnInfo(name = "image_caption") val caption: String,
    @ColumnInfo(name = "geo_location") val geoLocation: String,
    @ColumnInfo(name = "capture_time") val captureTime: String,
    @ColumnInfo(name = "image_resolution") val resolution: String,
    @ColumnInfo(name = "image_size") val size: String
)





