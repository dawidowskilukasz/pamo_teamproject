package com.example.workgood.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.workgood.R

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val END_ALARM = "com.example.workgood.END_ALARM"
    }

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
