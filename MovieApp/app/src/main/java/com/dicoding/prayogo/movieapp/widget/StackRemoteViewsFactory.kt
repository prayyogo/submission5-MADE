package com.dicoding.prayogo.movieapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.prayogo.movieapp.BuildConfig
import com.dicoding.prayogo.movieapp.R
import com.dicoding.prayogo.movieapp.database.MovieHelper
import com.dicoding.prayogo.movieapp.model.Film


class StackRemoteViewsFactory(context: Context):RemoteViewsService.RemoteViewsFactory {
    private val widgetItem = ArrayList<String>()
    private val itemName=ArrayList<String>()
    private val dataFilm=ArrayList<Film>()
    private var mContext: Context=context

    override fun onCreate() {
        getLoadData()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onDataSetChanged() {
       getLoadData()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv=RemoteViews(mContext.packageName, R.layout.item_widget)
        if(widgetItem.size!=0) {
            val bitmap = Glide.with(mContext)
                .asBitmap()
                .load(BuildConfig.BASE_URL_IMAGE + widgetItem[position])
                .submit().get()
            rv.setImageViewBitmap(R.id.imageView, bitmap)

            val extras = Bundle()
            extras.putString(FilmFavoriteWidget.EXTRA_ITEM, itemName[position])
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)

            rv.setOnClickFillInIntent(R.id.imageView,fillInIntent)
        }
        return rv
    }

    override fun getCount(): Int {
        return widgetItem.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
    }

    private fun getLoadData(){
        widgetItem.clear()
        itemName.clear()
        dataFilm.clear()
        // Movie
        val movieHelper= MovieHelper(mContext,true)
        movieHelper.open()
        if (movieHelper.query().isNotEmpty()){
            dataFilm.addAll(movieHelper.query())
        }
        movieHelper.close()
        // TV Show
        val tvShowHelper= MovieHelper(mContext,false)
        tvShowHelper.open()
        if (tvShowHelper.query().isNotEmpty()){
            dataFilm.addAll(tvShowHelper.query())
        }
        tvShowHelper.close()

        for(film in dataFilm){
            widgetItem.add(film.photo.toString())
            itemName.add(film.name.toString())
        }
    }
}