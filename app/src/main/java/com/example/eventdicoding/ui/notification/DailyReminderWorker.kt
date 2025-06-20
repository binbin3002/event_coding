package com.example.eventdicoding.ui.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventdicoding.data.EventRepository
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.data.local.room.EventDatabase
import com.example.eventdicoding.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.coroutineScope

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override  suspend fun  doWork(): Result = coroutineScope {
       try {
           val eventDatabase  = EventDatabase.getInstance(applicationContext)
           val repository = EventRepository.getInstance(
               ApiConfig.getApiService(),
               eventDatabase.eventDao(),
               eventDatabase.favoriteEventDao()
           )
           val nearestEvent = repository.getNearestActiveEvent()

           if (nearestEvent != null){
               showNotification(nearestEvent)
           }
           Result.success()
       } catch (e: Exception){
           Result.failure()
       }
    }
    private  fun showNotification(event: EventEntity){
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(event)
    }

}