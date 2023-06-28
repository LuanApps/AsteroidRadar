package com.udacity.asteroidradar.main

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException

class RefreshAsteroidsWorker(
    private val context: Context,
    params: WorkerParameters,
    private val mainViewModel: MainViewModel
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {
            mainViewModel.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}