package zhy.florence2_android.media.knowledgegraph.db

import androidx.room.Database
import androidx.room.RoomDatabase
import zhy.florence2_android.media.knowledgegraph.db.entity.Friend
import zhy.florence2_android.media.knowledgegraph.db.entity.Image
import zhy.florence2_android.media.knowledgegraph.db.entity.Person
import zhy.florence2_android.media.knowledgegraph.db.entity.PublicFigure
import zhy.florence2_android.media.knowledgegraph.db.entity.Relative
import zhy.florence2_android.media.knowledgegraph.db.entity.ShortMessage
import zhy.florence2_android.media.knowledgegraph.db.entity.TemporaryState
import zhy.florence2_android.media.knowledgegraph.db.entity.Thing

@Database(
    entities = [
        Image::class, ShortMessage::class,
        Thing::class,
        Person::class, TemporaryState::class, Relative::class, Friend::class, PublicFigure::class,
    ],
    version = 1
)
abstract class KnowledgeGraphDatabase : RoomDatabase() {
    abstract fun knowledgeGraphDao(): KnowledgeGraphDao
}