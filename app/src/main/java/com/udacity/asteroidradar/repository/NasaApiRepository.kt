package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
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

    val asteroids: LiveData<List<Asteroid>> = database.asteroidDatabaseDao.getSortedAsteroids()
    val pictureOfDay: LiveData<PictureOfDay> = database.pictureDatabaseDao.getLastPicture()

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

    suspend fun refreshPicture() {
        withContext(Dispatchers.IO) {
            try {
                val apiKey = Constants.APY_KEY
                val picture = NasaApi.nasaApiService.getPictureOfDay(apiKey)
                picture.let {
                    database.pictureDatabaseDao.insert(it)
                }
            } catch (e: Exception) {
                Log.e("repository", "Failed to refresh the picture of the day: $e")
            }
        }
    }

    suspend fun refreshPictureWithFake() {
        withContext(Dispatchers.IO) {
            try {
                val fakePicture = PictureOfDay(
                    id = 1,
                    mediaType = "image",
                    title = "Fake Picture",
                    url = "https://apod.nasa.gov/apod/image/2001/STSCI-H-p2006a-h-1024x614.jpg"
                )
                database.pictureDatabaseDao.insert(fakePicture)
            } catch (e: Exception) {
                Log.e("repository", "Failed to refresh the picture of the day: $e")
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