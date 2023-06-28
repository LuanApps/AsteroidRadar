package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiRepository
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.model.Asteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val database: AsteroidDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>> get() = _asteroids

    private val _navigateToDetails = MutableLiveData<Asteroid?>()
    val navigateToDetails: LiveData<Asteroid?> get() = _navigateToDetails

    private val nasaApiRepository = NasaApiRepository(NasaApi.nasaApiService)

    init {
        viewModelScope.launch {
            // Refresh asteroids on initialization
            refreshAsteroids()
        }
    }

    fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                // Check network connectivity
                if (isNetworkConnected()) {
                    val startDate = getCurrentDate()
                    val endDate = getEndDate()
                    val asteroidList = nasaApiRepository.getAsteroids(startDate, endDate)

                    // Save asteroids to the local database
                    saveAsteroidsToLocalDatabase(asteroidList)
                }

                // Fetch and display the asteroids from the database
                fetchAsteroidsFromLocalDatabase()

            } catch (e: Exception) {
                // Handle exception
                _asteroids.value = emptyList()
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check if the network capabilities include internet connectivity
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private suspend fun saveAsteroidsToLocalDatabase(asteroidList: List<Asteroid>) {
        withContext(Dispatchers.IO) {
            database.insertAll(asteroidList)
        }
    }

    private fun fetchAsteroidsFromLocalDatabase() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val asteroidsFromDb = database.getSortedAsteroids().sortedBy { it.closeApproachDate }
                _asteroids.postValue(asteroidsFromDb)
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetails.value = null
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