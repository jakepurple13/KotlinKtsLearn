package com.programmersbox.kotlinktslearn

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import com.programmersbox.gsonutils.getObjectExtra
import com.programmersbox.gsonutils.putExtra
import com.programmersbox.helpfulutils.notificationManager
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.loggingutils.fd

val KEY_TEXT_REPLY = "key_text_reply"

class ReplyService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println(intent?.extras?.keySet())
        Loged.f("Hello World")
        val f = RemoteInput.getResultsFromIntent(intent)
        f?.getCharSequence(KEY_TEXT_REPLY)?.let { Loged.f("$it is here") }
        val id = intent?.getIntExtra("notificationId", 0) ?: 0
        context?.notificationManager?.cancelAll()
    }
}

class ActionService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Loged.f("Hello World")
    }
}

fun Context.sendReplyNotification(
    smallIconId: Int,
    replyIconId: Int,
    replyActionText: String?,
    replyText: String,
    replyId: String,
    title: String?,
    message: String?,
    channel_id: String,
    gotoActivity: Class<*>,
    notification_id: Int,
    received: (String) -> Unit = {}
) {

    // The stack builder object will contain an artificial back stack for the
    // started Activity.
    // This ensures that navigating backward from the Activity leads out of
    // your app to the Home screen.
    val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)

    // Key for the string that's delivered in the action's intent.
    val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
        .setLabel(replyText)
        .build()

    val replyPendingIntent = PendingIntent.getBroadcast(
        this,
        0,
        Intent(this, ReplyService::class.java).apply {
            putExtra("notificationId", notification_id)
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create the reply action and add the remote input.
    val action = NotificationCompat.Action.Builder(
        replyIconId,
        replyActionText,
        replyPendingIntent
    )
        .addRemoteInput(remoteInput)
        .setAllowGeneratedReplies(true)
        .build()

    val pendingIntentAction = PendingIntent.getBroadcast(
        this,
        0,
        Intent(this, ActionService::class.java).apply {
            putExtra("notificationId", notification_id)
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create the reply action and add the remote input.
    val actionAction = NotificationCompat.Action.Builder(
        replyIconId,
        "Action",
        pendingIntentAction
    ).build()

    // The id of the channel.
    val mBuilder = NotificationCompat.Builder(this)
        .setSmallIcon(smallIconId)
        .setContentTitle(title)
        .addAction(action)
        .addAction(actionAction)
        .setContentText(message)
        .setChannelId(channel_id)
        .setAutoCancel(true)

    // mNotificationId is a unique integer your app uses to identify the
    // notification. For example, to cancel the notification, you can pass its ID
    // number to NotificationManager.cancel().
    notificationManager.notify(notification_id, mBuilder.build())
    Loged.fd("Notified")
}