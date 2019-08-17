package com.dicoding.prayogo.filmfavorite

import android.database.Cursor

interface LoadFilmCallback {
    fun postExecute(film: Cursor)
}