package com.example.workgood.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workgood.R
import java.util.*

class SetAlarmActivity : AppCompatActivity() {
    companion object {
        const val END_ALARM = "com.example.workgood.END_ALARM"
        const val ALARM_PREFS_KEY = "alarm_prefs"
        const val START_HOUR_KEY = "start_hour"
        const val START_MINUTE_KEY = "start_minute"
        const val END_HOUR_KEY = "end_hour"
        const val END_MINUTE_KEY = "end_minute"
    }

    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startTimePicker = findViewById<TimePicker>(R.id.start_time_picker)
        val endTimePicker = findViewById<TimePicker>(R.id.end_time_picker)
        val setAlarmButton = findViewById<Button>(R.id.set_alarm_button)

        startTimePicker.setIs24HourView(true)
        endTimePicker.setIs24HourView(true)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences(ALARM_PREFS_KEY, Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt(START_HOUR_KEY, 0)
        val startMinute = sharedPreferences.getInt(START_MINUTE_KEY, 0)
        val endHour = sharedPreferences.getInt(END_HOUR_KEY, 0)
        val endMinute = sharedPreferences.getInt(END_MINUTE_KEY, 0)


        startTimePicker.hour = startHour
        startTimePicker.minute = startMinute
        endTimePicker.hour = endHour
        endTimePicker.minute = endMinute

        setAlarmButton.setOnClickListener {
            val newStartHour = startTimePicker.hour
            val newStartMinute = startTimePicker.minute
            val newEndHour = endTimePicker.hour
            val newEndMinute = endTimePicker.minute

            if (newStartHour > newEndHour || (newStartHour == newEndHour && newStartMinute >= newEndMinute)) {
                Toast.makeText(this, getString(R.string.start_time_error), Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        setAlarms(newStartHour, newStartMinute, newEndHour, newEndMinute)
                    } else {
                        // Guide the user to the app settings to enable the permission
                        Toast.makeText(
                            this,
                            getString(R.string.exact_alarms_permission_message),
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }
                } else {
                    setAlarms(newStartHour, newStartMinute, newEndHour, newEndMinute)
                }
            }
        }
    }

    private fun setAlarms(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        // Save the times in SharedPreferences
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(ALARM_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(START_HOUR_KEY, startHour)
        editor.putInt(START_MINUTE_KEY, startMinute)
        editor.putInt(END_HOUR_KEY, endHour)
        editor.putInt(END_MINUTE_KEY, endMinute)
        editor.apply()

        setAlarm(endHour, endMinute, END_ALARM)

        finish() // Close the activity after setting the alarms
    }

    private fun setAlarm(hour: Int, minute: Int, action: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            intent.action = action
            PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        } catch (e: SecurityException) {
            //TODO: ADD asking again for permission
            Toast.makeText(
                this,
                getString(R.string.permission_not_granted_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
