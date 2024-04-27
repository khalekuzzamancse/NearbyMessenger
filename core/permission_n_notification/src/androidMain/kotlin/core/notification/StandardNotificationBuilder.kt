package core.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import core.permission.R
import kotlin.random.Random

// Define a class for standard notifications
 class StandardNotificationBuilder(context: Context) : BaseNotificationBuilder(context) {
     @SuppressLint("NotificationPermission")
     fun notify(title: String, message: String, notificationId: Int = Random.nextInt()) {
        val channelID = "channel_1"
        val channel = createChannel(channelId = channelID, channelName = "Channel_01")
        attachChannelToManager(channel)
        val notification = NotificationCompat.Builder(context, channelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notification_ic)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }
}