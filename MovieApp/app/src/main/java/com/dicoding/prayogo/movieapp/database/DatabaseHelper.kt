package com.dicoding.prayogo.movieapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_MOVIE)
        db.execSQL(SQL_CREATE_TABLE_TV_SHOW)
    }
    override fun onUpgrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_MOVIE)
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_TV_SHOW)
        onCreate(db)
    }
    companion object {
        var DATABASE_NAME = "dbfilmfavorite"
        private val DATABASE_VERSION = 1
        private val SQL_CREATE_TABLE_MOVIE = String.format(("CREATE TABLE %s"
                + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                " %s TEXT NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s DOUBLE NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s DOUBLE NOT NULL)"),
            DatabaseContract.TABLE_MOVIE,
            DatabaseContract.NoteColumns._ID,
            DatabaseContract.NoteColumns.PHOTO,
            DatabaseContract.NoteColumns.NAME,
            DatabaseContract.NoteColumns.DESCRIPTION,
            DatabaseContract.NoteColumns.RELEASEDATE,
            DatabaseContract.NoteColumns.RATING,
            DatabaseContract.NoteColumns.GENRE,
            DatabaseContract.NoteColumns.POPULARITY
        )

        private val SQL_CREATE_TABLE_TV_SHOW = String.format(("CREATE TABLE %s"
                + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                " %s TEXT NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s DOUBLE NOT NULL," +
                " %s TEXT NOT NULL," +
                " %s DOUBLE NOT NULL)"),
            DatabaseContract.TABLE_TV_SHOW,
            DatabaseContract.NoteColumns._ID,
            DatabaseContract.NoteColumns.PHOTO,
            DatabaseContract.NoteColumns.NAME,
            DatabaseContract.NoteColumns.DESCRIPTION,
            DatabaseContract.NoteColumns.RELEASEDATE,
            DatabaseContract.NoteColumns.RATING,
            DatabaseContract.NoteColumns.GENRE,
            DatabaseContract.NoteColumns.POPULARITY
        )
    }
}