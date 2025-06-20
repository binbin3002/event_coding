package com.example.eventdicoding.di

import android.content.Context
import com.example.eventdicoding.data.EventRepository
import com.example.eventdicoding.data.local.room.EventDatabase
import com.example.eventdicoding.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository{
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val favoriteEventDao = database.favoriteEventDao()
        return EventRepository.getInstance(apiService, dao, favoriteEventDao)
    }
    fun provideWorkManager(context: Context): androidx.work.WorkManager{
        return  androidx.work.WorkManager.getInstance(context)
    }
}