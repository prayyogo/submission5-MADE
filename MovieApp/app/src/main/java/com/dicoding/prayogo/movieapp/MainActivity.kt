package com.dicoding.prayogo.movieapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.TabLayout
import android.view.Menu
import android.view.MenuItem
import com.dicoding.prayogo.movieapp.adapter.PageAdapterFilm
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        var LANGUAGE_FILM:String?=null
        var TODAY:String?=null
        var SEARCH_FILM:String?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewpager_main.adapter = PageAdapterFilm(supportFragmentManager,this)
        LANGUAGE_FILM=resources.getString(R.string.language)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        TODAY= dateFormat.format(date).toString()
        supportActionBar?.elevation= 0F
        tabs_main.setupWithViewPager(viewpager_main)
        tabs_main.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        supportActionBar?.title = resources.getString(R.string.movies)
                    }
                    1 -> {
                        supportActionBar?.title = resources.getString(R.string.tv_shows)
                    }
                }
            }
        })

        supportActionBar?.title = resources.getString(R.string.movies)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_change_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.action_film_favorite -> {
                val mIntent = Intent(this, FavoriteFilmActivity::class.java)
                startActivity(mIntent)
            }
            R.id.action_notification_settings -> {
                val mIntent = Intent(this, NotificationActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
