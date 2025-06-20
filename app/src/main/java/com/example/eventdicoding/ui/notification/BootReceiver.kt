package com.example.eventdicoding.ui.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action){
            scheduleDailyReminder(context)
        }

    }
    private  fun scheduleDailyReminder(context: Context?){
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(context!!).enqueue(workRequest)
    }
}