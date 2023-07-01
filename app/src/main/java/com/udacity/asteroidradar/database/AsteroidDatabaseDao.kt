package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.model.Asteroid

@Dao
interface AsteroidDatabaseDao {

    @Query("DELETE FROM asteroid_table")
    fun clear()

    @Query("DELETE FROM asteroid_table WHERE closeApproachDate > date('now')")
    fun clearExceptToday()

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate >= date('now') ORDER BY closeApproachDate ASC")
    fun getSavedAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate == date('now') ORDER BY closeApproachDate ASC")
    fun getTodayAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate > date('now') ORDER BY closeApproachDate ASC")
    fun getNextWeekAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<Asteroid>)
}