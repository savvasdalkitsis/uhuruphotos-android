package com.savvasdalkitsis.uhuruphotos.log

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.michaelflisar.feedbackmanager.Feedback
import com.michaelflisar.feedbackmanager.FeedbackFile
import com.michaelflisar.lumberjack.core.CoreUtil
import java.io.File

/**
 * To be removed once https://github.com/MFlisar/Lumberjack/issues/14 is fixed
 */
fun showCrashNotification(
    context: Context,
    logFile: File?,
    receiver: String,
    appIcon: Int,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String = "Rare exception found",
    notificationText: String = "Please report this error by clicking this notification, thanks",
    subject: String = "Exception found in ${context.packageName}",
    titleForChooser: String = "Send report with",
    filesToAppend: List<File> = emptyList()
) {
    val allFiles = filesToAppend.toMutableList()
    logFile?.let { allFiles.add(0, it) }
    val feedback = Feedback(
        listOf(receiver),
        CoreUtil.getRealSubject(context, subject),
        attachments = allFiles.map { FeedbackFile.DefaultName(it) }
    )

    feedback.startNotificationFixed(
            context,
            titleForChooser,
            notificationTitle,
            notificationText,
            appIcon,
            notificationChannelId,
            notificationId
        )
}

private fun Feedback.startNotificationFixed(
    context: Context,
    chooserTitle: String,
    notificationTitle: String,
    notificationText: String,
    notificationIcon: Int,
    notificationChannel: String,
    notificationId: Int
) {
    val intent = buildIntent(context, chooserTitle)
    val pendingIntent = PendingIntent.getActivity(
            context,
            1111 /* unused */,
            intent,
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
                else -> FLAG_UPDATE_CURRENT
            }
        )
    val builder = NotificationCompat.Builder(
        context,
        notificationChannel
    )
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setContentText(notificationText)
        .setContentIntent(pendingIntent)
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}