package com.udacity.asteroidradar.main

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException
import com.udacity.asteroidradar.api.NasaApiRepository
import com.udacity.asteroidradar.database.AsteroidDatabase

class RefreshAsteroidsWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshAsteroidWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = NasaApiRepository(database)
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: retrofit2.HttpException) {
            Result.retry()
        }
    }
}