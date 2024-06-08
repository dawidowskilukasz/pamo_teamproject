package com.example.workgood.ui.settings

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.workgood.MainActivity
import com.example.workgood.R


/**
 * A foreground service that plays an alarm sound.
 * Once started, it will continue to play the sound in the background
 * until it is explicitly stopped by the user action or the service itself.
 *
 * The service creates an ongoing notification which keeps the service in the foreground,
 * reducing its chances of being killed by the system.
 */
class StartAlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "sound_playback_channel"

    /**
     * Processes the start command sent to the service.
     * It initializes and starts media playback and creates a notification for the ongoing foreground service.
     *
     * @param intent The Intent supplied to startService, as given.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's
     * current started state.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_ALARM_ACTION") {
            stopSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sound Playback Service")
            .setContentText("Playing sound in background")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    /**
     * This is a callback interface for the clients that bind to the service.
     * No binding to this service is provided, so it returns null.
     *
     * @param intent The Intent used to bind to this service.
     * @return Return an IBinder through which clients can call the service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Calls when the service is no longer used and is being destroyed.
     * Stops sound playback and releases the MediaPlayer resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Creates a notification channel for posting notifications to the system.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sound Playback Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

