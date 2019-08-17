package com.dicoding.prayogo.movieapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.prayogo.movieapp.model.Film
import kotlinx.android.synthetic.main.activity_detail_film.*

class DetailFilmActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_FILM="extra_film"
    }
    private var film= Film()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_film)
        if (savedInstanceState != null) {
            film = savedInstanceState.getParcelable("Film")
        } else {
            film = intent.getParcelableExtra(EXTRA_FILM)
        }
            img_photo.let {
                Glide
                    .with(applicationContext)
                    .load(BuildConfig.BASE_URL_IMAGE + film.photo)
                    .into(it)
            }
            tv_name.text = film.name
            tv_release_date.text = film.releaseDate
            tv_genre.text = film.genre
            tv_popularity.text = film.popularity.toString()
            tv_rating.text = film.rating.toString()
            tv_description.text = film.description
            supportActionBar?.title = film.name
    }
    @Override
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.clear()
        outState?.putParcelable("Film",film)
    }
}
