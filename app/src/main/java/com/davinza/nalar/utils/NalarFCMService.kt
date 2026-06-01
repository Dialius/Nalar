package com.davinza.nalar.utils

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NalarFCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        android.util.Log.d("NalarFCM", "New registration token generated: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Nalar"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val channelId = remoteMessage.data["channelId"] ?: NalarNotificationManager.CHANNEL_STREAK

        NalarNotificationManager.showNotification(applicationContext, title, body, channelId)
    }
}
