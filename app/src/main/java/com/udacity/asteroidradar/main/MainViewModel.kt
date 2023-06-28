package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiRepository
import com.udacity.asteroidradar.model.Asteroid
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>> get() = _asteroids

    private val _navigateToDetails = MutableLiveData<Asteroid?>()
    val navigateToDetails: LiveData<Asteroid?> get() = _navigateToDetails

    private val nasaApiRepository = NasaApiRepository(NasaApi.nasaApiService)


    init {
        // Initialize the list of asteroids (replace with your own logic)
//        val fakeAsteroids = createFakeAsteroids()
//        _asteroids.value = fakeAsteroids

        fetchAsteroids(getCurrentDate(), getEndDate())
    }

    private fun createFakeAsteroids(): List<Asteroid> {
        // Replace this with your own logic to create fake asteroids
        return listOf(
            Asteroid(1, "Asteroid 1", "2023-06-28", 5.0, 10.0, 10000.0, 500000.0, false),
            Asteroid(2, "Asteroid 2", "2023-06-29", 4.0, 8.0, 15000.0, 600000.0, true),
            Asteroid(3, "Asteroid 3", "2023-06-30", 6.0, 12.0, 8000.0, 400000.0, false)
        )
    }

    fun refreshAsteroids() {
        fetchAsteroids(getCurrentDate(), getEndDate())
    }

    fun onAsteroidClicked(asteroid: Asteroid){
        _navigateToDetails.value = asteroid
    }

    fun onAsteroidyNavigated() {
        _navigateToDetails.value = null
    }

    private fun fetchAsteroids(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                val asteroidList = nasaApiRepository.getAsteroids(startDate, endDate)
                _asteroids.value = asteroidList
            } catch (e: Exception) {
                // Handle exception
                _asteroids.value = emptyList()
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