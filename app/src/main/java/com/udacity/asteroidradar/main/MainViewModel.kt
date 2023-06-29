package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.repository.NasaApiRepository
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getInstance(application)
    private val repository = NasaApiRepository(database)

    val asteroids: LiveData<List<Asteroid>> = repository.asteroids
    val pictureOfDay: LiveData<PictureOfDay> = repository.pictureOfDay

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
        repository.refreshPicture()
//        repository.refreshPictureWithFake()
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetails.value = null
    }

}