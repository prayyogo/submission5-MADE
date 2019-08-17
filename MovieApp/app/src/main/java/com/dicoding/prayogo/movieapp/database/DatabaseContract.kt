package com.dicoding.prayogo.movieapp.database

import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns

open class KBaseColumns  {
    val _ID = "_id"
}
object DatabaseContract{
    var TABLE_MOVIE = "movie"
    var TABLE_TV_SHOW = "tvshow"
    val AUTHORITY="com.dicoding.prayogo.movieapp"
    val SCHEME="content"
    class NoteColumns private constructor(): BaseColumns {
        companion object: KBaseColumns() {
            var PHOTO="photo"
            val NAME = "name"
            val DESCRIPTION="description"
            val RELEASEDATE="releasedate"
            val RATING="rating"
            val GENRE="genre"
            val POPULARITY="popularity"

            val CONTENT_URI_MOVIE= Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_MOVIE)
                .build()

            val CONTENT_URI_TV_SHOW= Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_TV_SHOW)
                .build()
        }
    }

    fun getColumnString(cursor: Cursor, columnName:String):String{
        return cursor.getString(cursor.getColumnIndex(columnName))
    }

    fun getColumnInr(cursor: Cursor, columnName:String):Int{
        return cursor.getInt(cursor.getColumnIndex(columnName))
    }

    fun getColumnLong(cursor: Cursor, columnName:String):Long{
        return cursor.getLong(cursor.getColumnIndex(columnName))
    }
}