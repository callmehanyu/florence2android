package zhy.florence2_android.media.knowledgegraph.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["sender", "send_time"])
data class ShortMessage(
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "send_time") val sendTime: String
)