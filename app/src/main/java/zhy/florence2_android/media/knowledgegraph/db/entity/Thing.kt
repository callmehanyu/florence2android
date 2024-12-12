package zhy.florence2_android.media.knowledgegraph.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Thing(
    @PrimaryKey @ColumnInfo(name = "thing_id") val thingId: String,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "ocr_text") val ocrText: String,
    @ColumnInfo(name = "thing_caption") val caption: String
)
