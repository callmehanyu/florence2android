package zhy.florence2_android.media.knowledgegraph.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Person(
    @PrimaryKey @ColumnInfo(name = "person_id") val personId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "birth_date") val birthDate: String
)

@Entity
data class TemporaryState(
    @PrimaryKey @ColumnInfo(name = "person_id") val personId: String,
    @ColumnInfo(name = "posture") val posture: String,
    @ColumnInfo(name = "expression") val expression: String
)

@Entity
data class Relative(
    @PrimaryKey @ColumnInfo(name = "person_id") val personId: String,
    @ColumnInfo(name = "relationship") val relationship: String
)

@Entity
data class Friend(
    @PrimaryKey @ColumnInfo(name = "person_id") val personId: String,
    @ColumnInfo(name = "relationship_start_time") val relationshipStartTime: String,
    @ColumnInfo(name = "intimacy_level") val intimacyLevel: String
)

@Entity
data class PublicFigure(
    @PrimaryKey @ColumnInfo(name = "person_id") val personId: String
)
