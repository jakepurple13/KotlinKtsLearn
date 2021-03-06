package com.programmersbox.kotlinktslearn

import com.programmersbox.helpfulutils.notificationManager
import com.programmersbox.helpfulutils.whatIfNotNull
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import kotlin.properties.Delegates

enum class NotificationChannelImportance(internal val importance: Int) {
    /**
     * A notification with no importance: does not show in the shade.
     */
    NONE(0),

    /**
     * Min notification importance: only shows in the shade, below the fold.  This should
     * not be used with {@link Service#startForeground(int, Notification) Service.startForeground}
     * since a foreground service is supposed to be something the user cares about so it does
     * not make semantic sense to mark its notification as minimum importance.  If you do this
     * as of Android version {@link android.os.Build.VERSION_CODES#O}, the system will show
     * a higher-priority notification about your app running in the background.
     */
    MIN(1),

    /**
     * Low notification importance: Shows in the shade, and potentially in the status bar
     * (see {@link #shouldHideSilentStatusBarIcons()}), but is not audibly intrusive.
     */
    LOW(2),

    /**
     * Default notification importance: shows everywhere, makes noise, but does not visually
     * intrude.
     */
    DEFAULT(3),

    /**
     * Higher notification importance: shows everywhere, makes noise and peeks. May use full screen
     * intents.
     */
    HIGH(4),

    /**
     * Unused.
     */
    MAX(5)
}

/**
 * Creates a [NotificationChannel]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationChannel(
    id: String,
    name: CharSequence = id,
    importance: NotificationChannelImportance = NotificationChannelImportance.DEFAULT,
    block: NotificationChannel.() -> Unit = {}
) = notificationManager.createNotificationChannel(NotificationChannel(id, name, importance.importance).apply(block))

/**
 * Creates a [NotificationChannelGroup]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationGroup(id: String, name: CharSequence = id, block: NotificationChannelGroup.() -> Unit = {}) =
    notificationManager.createNotificationChannelGroup(NotificationChannelGroup(id, name).apply(block))

/**
 * sendNotification - sends a notification
 * @param smallIconId the icon id for the notification
 * @param title the title
 * @param message the message
 * @param channelId the channel id
 * @param gotoActivity the activity that will launch when notification is pressed
 * @param notificationId the id of the notification
 */
fun Context.sendNotification(
    @DrawableRes smallIconId: Int,
    title: String?,
    message: String?,
    notificationId: Int,
    channelId: String,
    groupId: String = channelId,
    gotoActivity: Class<*>? = null
) {
    // mNotificationId is a unique integer your app uses to identify the
    // notification. For example, to cancel the notification, you can pass its ID
    // number to NotificationManager.cancel().
    notificationManager.notify(
        notificationId,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) oAndGreaterNotification(smallIconId, title, message, channelId, groupId, gotoActivity)
        else compatNotification(smallIconId, title, message, channelId, groupId, gotoActivity)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.oAndGreaterNotification(
    @DrawableRes smallIconId: Int,
    title: String?,
    message: String?,
    channelId: String,
    groupId: String,
    gotoActivity: Class<*>? = null
): Notification = Notification.Builder(this, channelId)
    .setSmallIcon(smallIconId)
    .setContentTitle(title)
    .setContentText(message)
    .setGroup(groupId)
    .setContentIntent(pendingActivity(gotoActivity))
    .build()

private fun Context.compatNotification(
    @DrawableRes smallIconId: Int,
    title: String?,
    message: String?,
    channelId: String,
    groupId: String,
    gotoActivity: Class<*>? = null
): Notification = NotificationCompat.Builder(this, channelId)
    .setSmallIcon(smallIconId)
    .setContentTitle(title)
    .setContentText(message)
    .setGroup(groupId)
    .setContentIntent(pendingActivity(gotoActivity))
    .build()

private fun Context.pendingActivity(gotoActivity: Class<*>?) = gotoActivity?.let {
    TaskStackBuilder.create(this)
        .addParentStack(gotoActivity)
        .addNextIntent(Intent(this, it))
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.sendNotification(notificationId: Int, block: NotificationDslBuilder.() -> Unit) =
    notificationManager.notify(notificationId, NotificationDslBuilder.builder(this, block))

@DslMarker
annotation class NotificationUtilsMarker

@DslMarker
annotation class NotificationActionMarker

class NotificationDslBuilder(private val context: Context) {

    private val sdk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @NotificationUtilsMarker
    var channelId: String by Delegates.notNull()

    @NotificationUtilsMarker
    var groupId: String = ""

    @NotificationUtilsMarker
    var title: CharSequence? = null

    @NotificationUtilsMarker
    var message: CharSequence? = null

    @DrawableRes
    @NotificationUtilsMarker
    var smallIconId: Int? = null

    private var pendingActivity: PendingIntent? = null

    @NotificationUtilsMarker
    fun pendingActivity(gotoActivity: Class<*>?, block: TaskStackBuilder.() -> Unit = {}) {
        pendingActivity = gotoActivity?.let {
            TaskStackBuilder.create(context)
                .addParentStack(gotoActivity)
                .addNextIntent(Intent(context, it))
                .apply(block)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    sealed class NotificationAction(
        private val context: Context
    ) {
        @NotificationActionMarker
        var actionTitle: CharSequence by Delegates.notNull()

        @NotificationActionMarker
        var actionIcon: Int by Delegates.notNull()

        class Reply(context: Context) : NotificationAction(context) {

            @NotificationActionMarker
            var resultKey: String by Delegates.notNull()

            @NotificationActionMarker
            var label: String by Delegates.notNull()

            fun buildRemoteInput() = RemoteInput.Builder(resultKey)
                .setLabel(label)
                .build()

            fun buildRemoteInputSdk() = android.app.RemoteInput.Builder(resultKey)
                .setLabel(label)
                .build()

        }

        class Action(context: Context) : NotificationAction(context)

        internal fun buildSdk() = Notification.Action.Builder(actionIcon, actionTitle, pendingIntentAction)
            .also { if (this is Reply) it.addRemoteInput(buildRemoteInputSdk()) }
            .build()

        internal fun build() = NotificationCompat.Action.Builder(actionIcon, actionTitle, pendingIntentAction)
            .also { if (this is Reply) it.addRemoteInput(buildRemoteInput()) }
            .build()

        private var pendingIntentAction: PendingIntent? = null

        @NotificationActionMarker
        fun pendingActivity(gotoActivity: Class<*>, block: Intent.() -> Unit = {}) {
            pendingIntentAction = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, gotoActivity).apply(block),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @NotificationActionMarker
        fun pendingActivity(pendingIntent: PendingIntent?) {
            pendingIntentAction = pendingIntent
        }

    }

    private val actions = mutableListOf<NotificationAction>()

    @NotificationUtilsMarker
    fun addReplyAction(block: NotificationAction.Reply.() -> Unit) {
        actions.add(NotificationAction.Reply(context).apply(block))
    }

    @NotificationUtilsMarker
    fun addAction(block: NotificationAction.Action.() -> Unit) {
        actions.add(NotificationAction.Action(context).apply(block))
    }

    fun replyAction(block: NotificationAction.Reply.() -> Unit) = NotificationAction.Reply(context).apply(block)
    fun actionAction(block: NotificationAction.Action.() -> Unit) = NotificationAction.Action(context).apply(block)

    operator fun NotificationAction.unaryPlus() = actions.add(this).let { Unit }

    private fun build() = if (sdk) {
        Notification.Builder(context, channelId)
            .whatIfNotNull(smallIconId) { setSmallIcon(it) }
            .setContentTitle(title)
            .setContentText(message)
            .setGroup(groupId.let { if (it.isEmpty()) channelId else it })
            .setContentIntent(pendingActivity)
            .also { builder -> actions.forEach { builder.addAction(it.buildSdk()) } }
            .build()
    } else {
        NotificationCompat.Builder(context, channelId)
            .whatIfNotNull(smallIconId) { setSmallIcon(it) }
            .setContentTitle(title)
            .setContentText(message)
            .setGroup(groupId.let { if (it.isEmpty()) channelId else it })
            .setContentIntent(pendingActivity)
            .also { builder -> actions.forEach { builder.addAction(it.build()) } }
            .build()
    }

    companion object {
        fun builder(context: Context, block: NotificationDslBuilder.() -> Unit): Notification = NotificationDslBuilder(context).apply(block).build()
    }

}