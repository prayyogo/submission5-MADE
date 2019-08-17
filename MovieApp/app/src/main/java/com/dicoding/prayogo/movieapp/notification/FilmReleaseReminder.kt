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
import com.dicoding.prayogo.movieapp.BuildConfig
import com.dicoding.prayogo.movieapp.api.ApiMovieDb
import com.dicoding.prayogo.movieapp.model.MovieDb
import com.dicoding.prayogo.movieapp.model.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class FilmReleaseReminder: BroadcastReceiver() {
    private var notifId = 2000
    private var resultTheMovieDb by Delegates.notNull<ArrayList<Result>>()

    private fun sendNotification(context: Context, title: String, desc: String, id: Int) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val uriTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(title)
            .setContentText(desc)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(uriTone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "11011",
                "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            builder.setChannelId("11011")
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(id, builder.build())
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, FilmReleaseReminder::class.java)
        return PendingIntent.getBroadcast(context, 1011, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setAlarm(context: Context) {
        val delay = 0
            cancelAlarm(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, FilmReleaseReminder::class.java)

            intent.putExtra("id", notifId)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                100, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + delay,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + delay, pendingIntent
                )
            }
            notifId += 1
        Toast.makeText(context, context.getString(R.string.release_reminder)+" "+context.getString(R.string.on), Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context))
        Toast.makeText(context, context.getString(R.string.release_reminder)+" "+context.getString(R.string.off), Toast.LENGTH_SHORT).show()
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        getDataFilmRelease(context)

    }
    @SuppressLint("CheckResult")
    fun getDataFilmRelease(context: Context?){
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val apiMovieDb = retrofit.create(ApiMovieDb::class.java)
        apiMovieDb.getReleaseMovie()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { theMovieDb: MovieDb ->
                    resultTheMovieDb = theMovieDb.results as ArrayList
                    if (context != null) {
                        val index=(0..resultTheMovieDb.size).random()
                        val movieTitle =resultTheMovieDb[index].title
                        val desc = context.getString(R.string.today_release) + " " + movieTitle
                        sendNotification(context, movieTitle, desc, 1011)
                    }
                },
                { t: Throwable ->
                    t.printStackTrace()
                },
                {

                }
            )
    }
}