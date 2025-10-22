package com.example.pillbestie.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pillbestie.data.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = Injection.provideMedicineRepository(context)
            val scheduler = NotificationScheduler(context)

            // Alarms need to be re-scheduled on reboot. This must be done
            // in a background thread as it can be a long-running operation.
            CoroutineScope(Dispatchers.IO).launch {
                val medicines = repository.allMedicines.first()
                medicines.forEach { medicine ->
                    scheduler.schedule(medicine)
                }
            }
        }
    }
}
