package com.dicoding.prayogo.filmfavorite.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dicoding.prayogo.filmfavorite.BuildConfig
import com.dicoding.prayogo.filmfavorite.R
import com.dicoding.prayogo.filmfavorite.model.Film
import java.util.ArrayList


class FilmAdapter (val context: Context): RecyclerView.Adapter<FilmAdapter.FilmViewholder>() {
    private val listNotes = ArrayList<Film>()
    private var activity: Activity?=null

    fun FilmAdapter(activity: Activity) {
        this.activity = activity
    }

    fun getListNotes(): ArrayList<Film> {
        return listNotes
    }

    fun setListNotes(listNotes: ArrayList<Film>) {
        this.listNotes.clear()
        this.listNotes.addAll(listNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film_favorite, parent, false)
        return FilmViewholder(view)
    }

    override fun onBindViewHolder(holder: FilmViewholder, position: Int) {
        holder.tvTitle.text = getListNotes()[position].name
        holder.tvDescription.text = getListNotes()[position].description
        Glide
            .with(context)
            .load(BuildConfig.BASE_URL_IMAGE + getListNotes()[position].photo)
            .into(holder.imgPhoto)

    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    inner class FilmViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_photo)
    }
}