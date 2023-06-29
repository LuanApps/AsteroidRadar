package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.model.PictureOfDay

@Dao
interface PictureDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picture: PictureOfDay)

    @Query("SELECT * FROM picture_table ORDER BY id DESC LIMIT 1")
    fun getLastPicture(): LiveData<PictureOfDay>

    @Query("DELETE FROM picture_table")
    fun clear()
}