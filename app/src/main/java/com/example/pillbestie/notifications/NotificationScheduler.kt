package com.example.pillbestie.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.pillbestie.MainActivity
import com.example.pillbestie.data.Medicine

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(medicine: Medicine) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return // Permissions are handled by MainActivity
        }

        medicine.times.forEach { time ->
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("medicine", medicine.copy(times = listOf(time)))
            }
            val requestCode = "${medicine.id}:${time}".hashCode()
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val showAppIntent = Intent(context, MainActivity::class.java)
            val showAppPendingIntent = PendingIntent.getActivity(context, 0, showAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val alarmClockInfo = AlarmManager.AlarmClockInfo(time, showAppPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        }
    }

    fun cancel(medicine: Medicine) {
        medicine.times.forEach { time ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val requestCode = "${medicine.id}:${time}".hashCode()
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pendingIntent)
        }
    }
    
    fun scheduleSimpleNotification(title: String, message: String, notificationId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("simple_notification_title", title)
            putExtra("simple_notification_message", message)
            putExtra("simple_notification_id", notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
    }
}
