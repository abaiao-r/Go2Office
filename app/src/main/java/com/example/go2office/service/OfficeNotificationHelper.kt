package com.example.go2office.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class OfficeNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        createNotificationChannels()
    }
    fun showArrivalNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, "office_detection")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("Arrived at Office")
            .setContentText("You've arrived at Main Office")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(1001, notification)
        } catch (e: SecurityException) {
        }
    }
    fun showDepartureNotification(context: Context, hours: Float) {
        val notification = NotificationCompat.Builder(context, "office_detection")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Left Office")
            .setContentText("Session ended: ${"%.1f".format(hours)}h at office")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(1002, notification)
        } catch (e: SecurityException) {
        }
    }
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "office_detection",
                "Office Detection",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
