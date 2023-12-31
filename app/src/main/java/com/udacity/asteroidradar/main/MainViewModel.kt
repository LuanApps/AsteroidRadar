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

    private val _selectedOption = MutableLiveData<Options>()

    val asteroids: LiveData<List<Asteroid>> = _selectedOption.switchMap { option ->
        when (option) {
            Options.TODAY -> repository.todayAsteroids
            Options.SAVED -> repository.asteroids
            Options.NEXT_WEEK -> repository.nextWeekAsteroids
        }
    }

    val pictureOfDay: LiveData<PictureOfDay> = repository.pictureOfDay

    private val _navigateToDetails = MutableLiveData<Asteroid?>()
    val navigateToDetails: LiveData<Asteroid?> get() = _navigateToDetails

    init {
        viewModelScope.launch {
            // Refresh asteroids and pictureOfTheDay on initialization
            getData()
            showSavedAsteroids()
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

    fun showTodayAsteroids() {
        _selectedOption.value = Options.TODAY
    }

    fun showSavedAsteroids() {
        _selectedOption.value = Options.SAVED
    }

    fun showNextWeekAsteroids() {
        _selectedOption.value = Options.NEXT_WEEK
    }

    enum class Options {
        TODAY,
        SAVED,
        NEXT_WEEK
    }

}