package com.dicoding.prayogo.movieapp.adapter

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dicoding.prayogo.movieapp.BuildConfig
import com.dicoding.prayogo.movieapp.DetailFilmActivity
import com.dicoding.prayogo.movieapp.R
import com.dicoding.prayogo.movieapp.database.MovieHelper
import com.dicoding.prayogo.movieapp.model.Film
import com.dicoding.prayogo.movieapp.model.Genre
import com.dicoding.prayogo.movieapp.model.Result
import com.dicoding.prayogo.movieapp.widget.FilmFavoriteWidget
import kotlinx.android.synthetic.main.item_movie.view.*

class AdapterMovieDb (private val context: Context, private var resultMovieDb: ArrayList<Result>, private var genreMovieDb: ArrayList<Genre>, private var databaseMovie: ArrayList<Film>) : RecyclerView.Adapter<AdapterMovieDb.ViewHolderTheMovieDb>(){
    private var film=Film()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTheMovieDb =
        ViewHolderTheMovieDb(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_movie, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolderTheMovieDb, position: Int) {
        val resultItem = resultMovieDb[position]
        Glide
            .with(context)
            .load(BuildConfig.BASE_URL_IMAGE + resultItem.posterPath)
            .into(holder.itemView.img_photo)
        if(resultItem.title!=null){
            holder
                .itemView
                .tv_name
                ?.text = resultItem.title
        }else{
            holder
                .itemView
                .tv_name
                ?.text = resultItem.name
        }
        holder
            .itemView
            .tv_description
            ?.text = resultItem.overview
        // check database film
        var data=false
        if(databaseMovie.size!=0){
            for(movie in databaseMovie){
                if(movie.name == holder.itemView.tv_name.text.toString()){
                    data=true
                    break
                }
            }
        }
        // show detail film
        holder.itemView.setOnClickListener {
            if(resultItem.title!=null) {
                film = Film(0,
                    resultItem.posterPath,
                    holder.itemView.tv_name.text.toString(),
                    resultItem.overview,
                    resultItem.releaseDate,
                    resultItem.voteAverage,
                    getGenres(resultItem.genreIds),
                    resultItem.popularity
                )
            }else{
                film = Film(0,
                    resultItem.posterPath,
                    holder.itemView.tv_name.text.toString(),
                    resultItem.overview,
                    resultItem.firstAirDate,
                    resultItem.voteAverage,
                    getGenres(resultItem.genreIds),
                    resultItem.popularity
                )
            }
            showDetailFilm(film)
        }
        // click favorite button
        if(data){
            holder.itemView.btn_favorite.visibility=View.GONE
            holder.itemView.tv_favorite.visibility=View.VISIBLE
        }else{
            holder.itemView.btn_favorite.visibility=View.VISIBLE
            holder.itemView.tv_favorite.visibility=View.GONE
            holder.itemView.btn_favorite.setOnClickListener {
                holder.itemView.btn_favorite.visibility=View.GONE
                holder.itemView.tv_favorite.visibility=View.VISIBLE

                if(resultItem.title!=null) {
                    //val dbHandler = DatabaseMovieHelper(context, null,null,1)
                    val movieHelper=MovieHelper(context,true)
                    movieHelper.open()
                    film = Film(0,
                        resultItem.posterPath,
                        holder.itemView.tv_name.text.toString(),
                        resultItem.overview,
                        resultItem.releaseDate,
                        resultItem.voteAverage,
                        getGenres(resultItem.genreIds),
                        resultItem.popularity
                    )
                    movieHelper.insert(film)
                    movieHelper.close()
                   // dbHandler.addFilm(film)
                    //dbHandler.close()
                }else{
                   // val dbHandler = DatabaseTVShowHelper(context, null)
                    val tvShowHelper=MovieHelper(context,false)
                    tvShowHelper.open()
                    film = Film(0,
                        resultItem.posterPath,
                        holder.itemView.tv_name.text.toString(),
                        resultItem.overview,
                        resultItem.firstAirDate,
                        resultItem.voteAverage,
                        getGenres(resultItem.genreIds),
                        resultItem.popularity
                    )
                    tvShowHelper.insert(film)
                    tvShowHelper.close()
                   // dbHandler.addFilm(film)
                   // dbHandler.close()
                }
                // update film favorite widget
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val intent = Intent(context, FilmFavoriteWidget::class.java)
                val appWidgetId=appWidgetManager.getAppWidgetIds(ComponentName(context, FilmFavoriteWidget::class.java))
                intent.action=AppWidgetManager.ACTION_APPWIDGET_UPDATE
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
                context.sendBroadcast(intent)

                Toast.makeText(context, holder.itemView.tv_name.text.toString() +"  "+ context.getString(R.string.add_favorite), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount(): Int = resultMovieDb.size

  //  fun getItem(): ArrayList<Result> = resultMovieDb

    fun refreshAdapter(resultTheMovieDb: List<Result>) {
        this.resultMovieDb.addAll(resultTheMovieDb)
        notifyItemRangeChanged(0, this.resultMovieDb.size)
    }
    private fun getGenres(genreIds: List<Int>):  String {
        val movieGenres= arrayListOf<String>()
        var listGenre= String()
        for (genreId in genreIds) {
            for (genre in genreMovieDb) {
                if (genre.id == genreId) {
                    movieGenres.add(genre.name)
                    break
                }
            }
        }
        for(genre in movieGenres){
            if(movieGenres.size>1){
                listGenre+=genre+", "
            }else{
                listGenre+=genre
            }
        }
        return listGenre
    }
    private fun showDetailFilm(film: Film) {
        val dataFilm= Film(0,film.photo,film.name,film.description,film.releaseDate,film.rating,film.genre,film.popularity)
        val moveObjectIntent= Intent(context, DetailFilmActivity::class.java)
        moveObjectIntent.putExtra(DetailFilmActivity.EXTRA_FILM,dataFilm)
        context.startActivity(moveObjectIntent)
    }
    inner class ViewHolderTheMovieDb(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}