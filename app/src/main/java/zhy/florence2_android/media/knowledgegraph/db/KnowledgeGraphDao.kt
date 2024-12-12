package zhy.florence2_android.media.knowledgegraph.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import zhy.florence2_android.media.knowledgegraph.db.entity.Image

@Dao
interface KnowledgeGraphDao {
    @Query("SELECT * FROM image")
    fun getAll(): List<Image>

    @Query("SELECT * FROM image WHERE path IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Image>

    @Query("SELECT * FROM image WHERE path LIKE :path LIMIT 1")
    fun findByName(path: String): Image

    @Insert
    fun insertAll(vararg users: Image)

    @Delete
    fun delete(user: Image)
}