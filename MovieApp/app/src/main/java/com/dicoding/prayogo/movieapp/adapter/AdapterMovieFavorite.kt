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
import com.dicoding.prayogo.movieapp.*
import com.dicoding.prayogo.movieapp.database.DatabaseMovieHelper
import com.dicoding.prayogo.movieapp.database.DatabaseTVShowHelper
import com.dicoding.prayogo.movieapp.database.MovieHelper
import com.dicoding.prayogo.movieapp.model.Film
import com.dicoding.prayogo.movieapp.widget.FilmFavoriteWidget
import kotlinx.android.synthetic.main.item_movie_favorite.view.*

class AdapterMovieFavorite (private val context: Context, private var databaseMovie: ArrayList<Film>, private var data:Boolean) : RecyclerView.Adapter<AdapterMovieFavorite.ViewHolderTheMovieDb>() {

        private var film=Film()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTheMovieDb =
            ViewHolderTheMovieDb(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_movie_favorite, parent, false)
            )

    override fun onBindViewHolder(holder: ViewHolderTheMovieDb, position: Int) {
        val resultItem = databaseMovie[position]
        Glide
            .with(context)
            .load(BuildConfig.BASE_URL_IMAGE + resultItem.photo)
            .into(holder.itemView.img_photo)
        holder
            .itemView
            .tv_name
            ?.text = resultItem.name
        holder
            .itemView
            .tv_description
            ?.text = resultItem.description

        // show detail film
        holder.itemView.setOnClickListener {
                film = Film(0,
                    resultItem.photo,
                    holder.itemView.tv_name.text.toString(),
                    resultItem.description,
                    resultItem.releaseDate,
                    resultItem.rating,
                    resultItem.genre,
                    resultItem.popularity
                )
            showDetailFilm(film)
        }
        // delete
        holder.itemView.btn_delete.visibility=View.VISIBLE
        holder.itemView.btn_delete.setOnClickListener {
            holder.itemView.btn_delete.visibility=View.GONE
            if(data){
                removeMovie(position)
                val movieHelper = MovieHelper(context,true)
                movieHelper.open()
                movieHelper.delete(position+1)
                if(MoviesFavoriteFragment.dataMovie.size==0){
                    movieHelper.deleteAll()
                    MoviesFavoriteFragment.noDataText.visibility=View.VISIBLE
                }else{
                    movieHelper.updateFilm(MoviesFavoriteFragment.dataMovie)
                }
                movieHelper.close()
            }else{
                removeTVShow(position)
                val tvShowHelper = MovieHelper(context,false)
                tvShowHelper.open()
                tvShowHelper.delete(position+1)
                if(TVShowsFavoriteFragment.dataTVShow.size==0){
                    tvShowHelper.deleteAll()
                    TVShowsFavoriteFragment.noDataText.visibility=View.VISIBLE
                }else{
                    tvShowHelper.updateFilm(TVShowsFavoriteFragment.dataTVShow)
                }
                tvShowHelper.close()
            }
            // update film favorite widget
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val intent = Intent(context, FilmFavoriteWidget::class.java)
            val appWidgetId=appWidgetManager.getAppWidgetIds(ComponentName(context, FilmFavoriteWidget::class.java))
            intent.action= AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
            context.sendBroadcast(intent)

            Toast.makeText(context, holder.itemView.tv_name.text.toString() +"  "+ context.getString(R.string.delete_favorite), Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int = databaseMovie.size

  //  fun getItem(): ArrayList<Film> = databaseMovie

    // search film favorite
    fun setFilterMovie(filterFilm:ArrayList<Film>) {
        MoviesFavoriteFragment.dataMovie.clear()
        MoviesFavoriteFragment.dataMovie.addAll(filterFilm)
        notifyDataSetChanged()
    }
    fun setFilterTVShow(filterFilm:ArrayList<Film>) {
        TVShowsFavoriteFragment.dataTVShow.clear()
        TVShowsFavoriteFragment.dataTVShow.addAll(filterFilm)
        notifyDataSetChanged()
    }
    private fun removeMovie(position:Int) {
        MoviesFavoriteFragment.dataMovie.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, MoviesFavoriteFragment.dataMovie.size)
    }
    private fun removeTVShow(position:Int) {
        TVShowsFavoriteFragment.dataTVShow.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, TVShowsFavoriteFragment.dataTVShow.size)
    }
    private fun showDetailFilm(film: Film) {
        val dataFilm= Film(0,film.photo,film.name,film.description,film.releaseDate,film.rating,film.genre,film.popularity)
        val moveObjectIntent= Intent(context, DetailFilmActivity::class.java)
        moveObjectIntent.putExtra(DetailFilmActivity.EXTRA_FILM,dataFilm)
        context.startActivity(moveObjectIntent)
    }

    inner class ViewHolderTheMovieDb(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}