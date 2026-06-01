package com.davinza.nalar.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.davinza.nalar.MainActivity
import com.davinza.nalar.R

object NalarNotificationManager {
    private const val PREFS_NAME = "nalar_notification_prefs"
    const val CHANNEL_STREAK = "streak_reminders"
    const val CHANNEL_RANK = "leaderboard_alerts"
    const val CHANNEL_KEY = "energy_reminders"

    fun initialize(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Streak Channel
            val streakChannel = NotificationChannel(
                CHANNEL_STREAK,
                "Pengingat Belajar Harian",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Mengingatkan Anda untuk menjaga streak dan belajar setiap hari."
            }

            // 2. Rank Channel
            val rankChannel = NotificationChannel(
                CHANNEL_RANK,
                "Peringkat & Liga",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pemberitahuan ketika Anda disalip di papan peringkat atau promosi liga."
            }

            // 3. Energy Channel
            val energyChannel = NotificationChannel(
                CHANNEL_KEY,
                "Energi & Kunci",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi saat energi/kunci belajar Anda telah terisi penuh."
            }

            notificationManager.createNotificationChannel(streakChannel)
            notificationManager.createNotificationChannel(rankChannel)
            notificationManager.createNotificationChannel(energyChannel)
        }
    }

    fun isEnabled(context: Context, channelId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(channelId, true) // enabled by default
    }

    fun setEnabled(context: Context, channelId: String, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(channelId, enabled).apply()
    }

    fun showNotification(context: Context, title: String, message: String, channelId: String) {
        if (!isEnabled(context, channelId)) return

        // Intent to launch MainActivity when notification clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Decode colored logo for large icon inside notification drawer
        val largeIcon = try {
            android.graphics.BitmapFactory.decodeResource(
                context.resources,
                R.drawable.logo_nalar_premium
            )
        } catch (e: Exception) {
            null
        }

        // Using clean outline vector for small icon, and full-color logo for large icon
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .apply {
                if (largeIcon != null) {
                    setLargeIcon(largeIcon)
                }
            }
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(
                if (channelId == CHANNEL_RANK) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(channelId.hashCode(), builder.build())
        } catch (e: SecurityException) {
            // Android 13 runtime permission not granted
        }
    }
}
