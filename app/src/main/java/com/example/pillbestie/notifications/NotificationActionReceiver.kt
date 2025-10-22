package com.example.pillbestie.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillbestie.DoseUpdateWorker
import com.google.gson.Gson

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicine = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_MEDICINE, com.example.pillbestie.data.Medicine::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_MEDICINE)
        }
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val action = intent.action

        if (medicine == null || action == null) {
            return
        }

        val workManager = WorkManager.getInstance(context)
        val medicineJson = Gson().toJson(medicine)

        val workRequest = OneTimeWorkRequestBuilder<DoseUpdateWorker>()
            .setInputData(Data.Builder()
                .putString(DoseUpdateWorker.KEY_MEDICINE_JSON, medicineJson)
                .putString(DoseUpdateWorker.KEY_ACTION, action)
                .putInt(DoseUpdateWorker.KEY_NOTIFICATION_ID, notificationId)
                .build())
            .build()

        workManager.enqueue(workRequest)
    }

    companion object {
        const val ACTION_TAKE = "com.example.pillbestie.ACTION_TAKE"
        const val ACTION_SNOOZE = "com.example.pillbestie.ACTION_SNOOZE"
        const val EXTRA_MEDICINE = "extra_medicine"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
