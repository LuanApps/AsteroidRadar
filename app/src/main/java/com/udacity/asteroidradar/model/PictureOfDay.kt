package com.udacity.asteroidradar.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "picture_table")
data class PictureOfDay(
    @PrimaryKey(autoGenerate = false)
    //I want only one instance of PictureOfDay in database, so every time the app gets a new PictureOfDay, it will replace the older instance. To Achieve this I`m putting the same id in PictureOfDay object before save it in database. the @Insert(onConflict = OnConflictStrategy.REPLACE) annotation will replace the data.
    val id: Long = 1L,
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String):Parcelable