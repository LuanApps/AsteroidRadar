package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiRepository
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getInstance(application)
    private val repository = NasaApiRepository(database)

    val asteroids: LiveData<List<Asteroid>> = repository.asteroids

    private val _navigateToDetails = MutableLiveData<Asteroid?>()
    val navigateToDetails: LiveData<Asteroid?> get() = _navigateToDetails

    init {
        viewModelScope.launch {
            // Refresh asteroids and pictureOfTheDay on initialization
            getData()
        }
    }

    suspend fun getData() {
        repository.refreshAsteroids()
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetails.value = null
    }

}