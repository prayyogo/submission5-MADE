package com.dicoding.prayogo.movieapp.helper

import android.database.Cursor
import android.provider.BaseColumns._ID
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.GENRE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.NAME
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.PHOTO
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.POPULARITY
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.RATING
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.RELEASEDATE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.dicoding.prayogo.movieapp.model.Film

class MappingHelper {
    companion object {
        fun mapCursorToArrayList(filmCursor: Cursor): ArrayList<Film> {
            val filmList = ArrayList<Film>()
            while (filmCursor.moveToNext()) {
                val id = filmCursor.getInt(filmCursor.getColumnIndexOrThrow(_ID))
                val photo = filmCursor.getString(filmCursor.getColumnIndexOrThrow(PHOTO))
                val name = filmCursor.getString(filmCursor.getColumnIndexOrThrow(NAME))
                val description = filmCursor.getString(filmCursor.getColumnIndexOrThrow(DESCRIPTION))
                val date = filmCursor.getString(filmCursor.getColumnIndexOrThrow(RELEASEDATE))
                val rating = filmCursor.getDouble(filmCursor.getColumnIndexOrThrow(RATING))
                val genre = filmCursor.getString(filmCursor.getColumnIndexOrThrow(GENRE))
                val popularity = filmCursor.getDouble(filmCursor.getColumnIndexOrThrow(POPULARITY))
                filmList.add(Film(id, photo, name, description, date, rating, genre, popularity))
            }
            return filmList
        }
    }
}