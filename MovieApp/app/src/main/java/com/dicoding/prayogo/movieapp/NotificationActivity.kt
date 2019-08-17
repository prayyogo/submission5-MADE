package com.dicoding.prayogo.movieapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.prayogo.movieapp.notification.FilmDailyReminder
import com.dicoding.prayogo.movieapp.notification.FilmReleaseReminder
import kotlinx.android.synthetic.main.activity_notification.*
import kotlin.properties.Delegates

class NotificationActivity : AppCompatActivity() {

    private var filmDailyRemider=FilmDailyReminder()
    private var filmReleaseReminder=FilmReleaseReminder()
    private var isDaily by Delegates.notNull<Boolean>()
    private var isRelease by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val sharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if(savedInstanceState!=null){
            isDaily=savedInstanceState.getBoolean("Daily")
            isRelease=savedInstanceState.getBoolean("Release")
        }else{
            isDaily=sharedPreferences.getBoolean("Daily", false)
            isRelease=sharedPreferences.getBoolean("Release", false)
        }
        switch_daily_reminder.isChecked=isDaily
        switch_release_reminder.isChecked=isRelease
        supportActionBar?.title = resources.getString(R.string.notifications)

        switch_daily_reminder.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                filmDailyRemider.setAlarm(applicationContext)
            }else{
                filmDailyRemider.cancelAlarm(applicationContext)
            }
            isDaily=isChecked
            editor.putBoolean("Daily",isDaily)
            editor.apply()
        }

        switch_release_reminder.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                filmReleaseReminder.setAlarm(applicationContext)
            }else{
                filmReleaseReminder.cancelAlarm(applicationContext)
            }
            isRelease=isChecked
            editor.putBoolean("Release",isRelease)
            editor.apply()
        }
    }
    @Override
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("Daily",isDaily)
        outState?.putBoolean("Release",isRelease)
    }
}
