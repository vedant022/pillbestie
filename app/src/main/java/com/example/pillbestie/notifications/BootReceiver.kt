package com.example.pillbestie.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule your alarms here. You'll need to fetch your stored
            // alarms from a database or shared preferences and reschedule them.
        }
    }
}
