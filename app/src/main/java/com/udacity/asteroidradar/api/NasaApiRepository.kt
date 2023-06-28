package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.util.Constants
import com.udacity.asteroidradar.model.Asteroid
import org.json.JSONObject

class NasaApiRepository(private val apiService: NasaApiService) {
    suspend fun getAsteroids(startDate: String, endDate: String): List<Asteroid> {
        val apiKey = Constants.APY_KEY
        val response = apiService.getAsteroids(startDate, endDate, apiKey)
        val jsonString = response.string()
        val jsonObject = JSONObject(jsonString)
        return parseAsteroidsJsonResult(jsonObject)
    }
}