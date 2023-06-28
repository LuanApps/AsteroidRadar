package com.udacity.asteroidradar.database

import androidx.room.*
import com.udacity.asteroidradar.model.Asteroid

@Dao
interface AsteroidDatabaseDao {

    @Insert
    suspend fun insert(asteroid: Asteroid)

    @Update
    suspend fun update(asteroid: Asteroid)

    @Query("DELETE FROM asteroid_table")
    suspend fun clear()

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate >= date('now')")
    suspend fun getAsteroids(): List<Asteroid>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate >= date('now') ORDER BY closeApproachDate ASC")
    suspend fun getSortedAsteroids(): List<Asteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asteroids: List<Asteroid>)
}