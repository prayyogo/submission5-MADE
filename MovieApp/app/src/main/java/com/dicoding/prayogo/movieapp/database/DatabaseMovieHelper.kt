package com.dicoding.prayogo.movieapp.database

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.dicoding.prayogo.movieapp.model.Film
import com.dicoding.prayogo.movieapp.provider.FilmProvider

class DatabaseMovieHelper (context: Context, name: String?,
                           factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {

    private var contentResolver: ContentResolver ?= null

    init {
        contentResolver = context.contentResolver
    }
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_PHOTO + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_RELEASEDATE+ " TEXT," +
                COLUMN_RATING + " DOUBLE," +
                COLUMN_GENRE + " TEXT," +
                COLUMN_POPULARITY + " DOUBLE" + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun addFilm(film: Film) {
        val values = ContentValues()
        values.put(COLUMN_PHOTO, film.photo)
        values.put(COLUMN_NAME, film.name)
        values.put(COLUMN_DESCRIPTION, film.description)
        values.put(COLUMN_RELEASEDATE, film.releaseDate)
        values.put(COLUMN_RATING, film.rating)
        values.put(COLUMN_GENRE, film.genre)
        values.put(COLUMN_POPULARITY, film.popularity)
        //contentResolver?.insert(FilmProvider.CONTENT_URI,values)
        /*val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()*/
    }
    fun getAllFilm(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    fun updateFilm(film: ArrayList<Film>) {
        deleteAll()
        for(dataFilm in film) {
            addFilm(dataFilm)
        }
    }
    fun deleteFilm(id: Int){
     //   contentResolver?.delete(FilmProvider.CONTENT_URI,id.toString(),null)
       /* val db = this.writableDatabase
        db.delete(TABLE_NAME, COLUMN_ID+ " = '" + id + "'",null)
        db.close()*/
    }
    fun deleteAll(){
        val db = this.writableDatabase
        db.delete(TABLE_NAME,null,null)
        db.close()
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Movie.db"
        val AUTHORITY="com.dicoding.prayogo.movieapp"
        val SCHEME="scheme"
        val TABLE_NAME = "MovieFavorite"
        val COLUMN_ID = "_id"
        val COLUMN_PHOTO="photo"
        val COLUMN_NAME = "name"
        val COLUMN_DESCRIPTION="description"
        val COLUMN_RELEASEDATE="releasedate"
        val COLUMN_RATING="rating"
        val COLUMN_GENRE="genre"
        val COLUMN_POPULARITY="popularity"
        val CONTENT_URI=Uri.Builder().scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(TABLE_NAME)
            .build()

        fun getColumnString(cursor: Cursor,columnName:String):String{
            return cursor.getString(cursor.getColumnIndex(columnName))
        }

        fun getColumnInr(cursor: Cursor,columnName:String):Int{
            return cursor.getInt(cursor.getColumnIndex(columnName))
        }

        fun getColumnLong(cursor: Cursor,columnName:String):Long{
            return cursor.getLong(cursor.getColumnIndex(columnName))
        }
    }
}