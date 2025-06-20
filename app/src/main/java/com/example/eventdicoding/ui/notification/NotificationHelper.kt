package com.example.eventdicoding.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.eventdicoding.R
import androidx.core.app.NotificationCompat
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.ui.detail.DetailActivity
import com.example.eventdicoding.util.SimpleDateUtil.formatDateTime

class NotificationHelper(private  val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Event Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(event: EventEntity) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent : PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val shortEventName = if (event.name.length > 30)"${event.name.substring(0, 30)}..." else event.name
        val formattedDate = formatDateTime(event.beginTime)
        val notificationText = "$shortEventName starts at $formattedDate"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_dicoding)
            .setContentTitle("Upcoming Event")
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

    }
    companion object{
        private const val  CHANNEL_ID = "event_reminder_channel"
        private  const val  NOTIFICATION_ID = 1
    }
}