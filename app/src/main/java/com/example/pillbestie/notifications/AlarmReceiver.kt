package com.example.pillbestie.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pillbestie.R
import com.example.pillbestie.data.Medicine
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Check if this is a simple notification (for refills)
        if (intent.hasExtra("simple_notification_title")) {
            val title = intent.getStringExtra("simple_notification_title") ?: ""
            val message = intent.getStringExtra("simple_notification_message") ?: ""
            val notificationId = intent.getIntExtra("simple_notification_id", 0)
            
            val notification = NotificationCompat.Builder(context, "medication_reminders")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, notification)
            }
            return
        }

        // This is a full medication alarm
        val medicine = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("medicine", Medicine::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra("medicine")
        }
        medicine ?: return

        val notificationId = "${medicine.id}:${medicine.times.first()}".hashCode()

        // Intent for the "Take" action
        val takeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_TAKE
            putExtra(NotificationActionReceiver.EXTRA_MEDICINE, medicine)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val takePendingIntent = PendingIntent.getBroadcast(context, notificationId + 1, takeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Intent for the "Snooze" action
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra(NotificationActionReceiver.EXTRA_MEDICINE, medicine)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(context, notificationId + 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeString = timeFormat.format(medicine.times.first())

        val notification = NotificationCompat.Builder(context, "medication_alarms_v2")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Medicine Reminder")
            .setContentText("${medicine.name} (${medicine.dosage}) - $timeString")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .addAction(0, "Taken", takePendingIntent)
            .addAction(0, "Snooze", snoozePendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }
}
