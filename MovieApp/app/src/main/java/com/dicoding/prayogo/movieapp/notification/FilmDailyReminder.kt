package com.dicoding.prayogo.movieapp.notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.app.AlarmManager
import android.os.Build
import android.app.PendingIntent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.support.v4.content.ContextCompat
import com.dicoding.prayogo.movieapp.R
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.dicoding.prayogo.movieapp.MainActivity
import java.util.*


@Suppress("DEPRECATION")
class FilmDailyReminder : BroadcastReceiver() {

    private fun sendNotification(context: Context, title: String, desc: String, id: Int) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val uriTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(title)
            .setContentText(desc)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(uriTone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "11001", "NOTIFICATION_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            builder.setChannelId("11001")
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(id, builder.build())

    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, FilmDailyReminder::class.java)
        return PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setAlarm(context: Context) {
        cancelAlarm(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                getPendingIntent(context)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                getPendingIntent(context)
            )
        }

        Toast.makeText(context, context.getString(R.string.daily_reminder)+" "+context.getString(R.string.on), Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context) {
        val mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.cancel(getPendingIntent(context))

        Toast.makeText(context, context.getString(R.string.daily_reminder)+" "+context.getString(R.string.off), Toast.LENGTH_SHORT).show()
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            sendNotification(context, context.getString(R.string.daily_title),
                context.getString(R.string.daily_desc), 1001)
        }
    }
}