package com.example.workgood.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.workgood.R

/**
 * A broadcast receiver that handles alarm-related broadcasts.
 *
 * This class is responsible for receiving intents broadcasted by the Android system
 * at the times specified by alarms set within the application.
 */
class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val END_ALARM = "com.example.workgood.END_ALARM"
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an intent broadcast.
     * Depending on the action specified in the received intent, the receiver
     * performs the corresponding action (e.g., starting the alarm service).
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received that contains information about the broadcast.
     */
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            END_ALARM -> {
                val message = context.getString(R.string.end_time_reached_message)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                val serviceIntent = Intent(context, StartAlarmService::class.java)
                context.startService(serviceIntent)
            }
        }
    }
}
