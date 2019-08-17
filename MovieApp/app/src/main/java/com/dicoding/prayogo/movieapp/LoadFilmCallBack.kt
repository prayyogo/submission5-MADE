package com.dicoding.prayogo.movieapp

import android.database.Cursor

interface LoadFilmCallBack {
    fun preExecute()

    fun postExecute(film: Cursor)
}