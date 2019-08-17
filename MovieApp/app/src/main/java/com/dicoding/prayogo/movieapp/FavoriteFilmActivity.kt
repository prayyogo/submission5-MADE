package com.dicoding.prayogo.movieapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.TabLayout
import android.view.Menu
import android.view.MenuItem
import com.dicoding.prayogo.movieapp.adapter.PageAdapterFavorite
import kotlinx.android.synthetic.main.activity_favorite_film.*

class FavoriteFilmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_film)
        viewpager_favorite.adapter = PageAdapterFavorite(supportFragmentManager,this)
        MainActivity.LANGUAGE_FILM =resources.getString(R.string.language)
        supportActionBar?.elevation= 0F
        tabs_favorite.setupWithViewPager(viewpager_favorite)
        tabs_favorite.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        supportActionBar?.title = resources.getString(R.string.movies_favorite)
                    }
                    1 -> {
                        supportActionBar?.title = resources.getString(R.string.tv_shows_favorite)
                    }
                }
            }
        })

        supportActionBar?.title = resources.getString(R.string.movies_favorite)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.favorite_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_change_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.action_film_catalogue -> {
                val mIntent = Intent(this, MainActivity::class.java)
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
