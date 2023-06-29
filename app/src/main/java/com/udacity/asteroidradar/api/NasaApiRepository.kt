package com.udacity.asteroidradar.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.util.Constants
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NasaApiRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = database.asteroidDatabaseDao.getAsteroids()

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {

                val apiKey = Constants.APY_KEY
                val response = NasaApi.nasaApiService.getAsteroids(getCurrentDate(), getEndDate(), apiKey)
                val jsonString = response.string()
                val jsonObject = JSONObject(jsonString)
                val asteroidsList = parseAsteroidsJsonResult(jsonObject)
                if(asteroidsList.size > 0){
                    database.asteroidDatabaseDao.clear()
                }
                database.asteroidDatabaseDao.insertAll(asteroidsList)
            } catch (e: Exception) {
                Log.e("repository", "Failed to refresh the asteroid data: $e")
            }
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun getEndDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7) // Add 7 days to the current date
        val endDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(endDate)
    }
}