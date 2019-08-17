package com.dicoding.prayogo.movieapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.dicoding.prayogo.movieapp.MainActivity
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.CONTENT_URI_MOVIE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.CONTENT_URI_TV_SHOW
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.GENRE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.NAME
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.PHOTO
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.POPULARITY
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.RATING
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.RELEASEDATE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion._ID
import com.dicoding.prayogo.movieapp.database.DatabaseContract.TABLE_MOVIE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.TABLE_TV_SHOW
import com.dicoding.prayogo.movieapp.model.Film
import java.util.ArrayList

class MovieHelper constructor(context: Context,tableMovie:Boolean) {
    private val dataBaseHelper: DatabaseHelper = DatabaseHelper(context)

    private var database: SQLiteDatabase? = null
   private var movie=tableMovie
    private var contexts=context

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
        DATABASE_TABLE = if(movie){
            TABLE_MOVIE
        }else{
            TABLE_TV_SHOW
        }
    }

    fun close() {
        dataBaseHelper.close()

        if (database!!.isOpen)
            database!!.close()
    }

    fun query(): ArrayList<Film> {
        val arrayList = ArrayList<Film>()
        val cursor = database!!.query(DATABASE_TABLE, null, null, null, null, null, "${BaseColumns._ID} DESC", null)
        cursor.moveToFirst()
        var film: Film
        if (cursor.count > 0) {
            do {
                film = Film()
                film.id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                film.photo = cursor.getString(cursor.getColumnIndexOrThrow(PHOTO))
                film.name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                film.description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                film.releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(RELEASEDATE))
                film.rating = cursor.getDouble(cursor.getColumnIndexOrThrow(RATING))
                film.genre = cursor.getString(cursor.getColumnIndexOrThrow(GENRE))
                film.popularity = cursor.getDouble(cursor.getColumnIndexOrThrow(POPULARITY))
                arrayList.add(film)
                cursor.moveToNext()

            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    /**
     * Gunakan method ini untuk query insert
     *
     * @param note model note yang akan dimasukkan
     * @return id dari data yang baru saja dimasukkan
     */
    fun insert(film: Film): Long {
        val initialValues = ContentValues()
        initialValues.put(PHOTO, film.photo)
        initialValues.put(NAME, film.name)
        initialValues.put(DESCRIPTION, film.description)
        initialValues.put(RELEASEDATE, film.releaseDate)
        initialValues.put(RATING, film.rating)
        initialValues.put(GENRE, film.genre)
        initialValues.put(POPULARITY, film.popularity)
        if(movie){
            contexts.contentResolver.insert(CONTENT_URI_MOVIE,initialValues)
        }else{
            contexts.contentResolver.insert(CONTENT_URI_TV_SHOW,initialValues)
        }
        return database!!.insert(DATABASE_TABLE, null, initialValues)
    }

    fun update(film: Film): Int {
        val args = ContentValues()
        args.put(PHOTO, film.photo)
        args.put(NAME, film.name)
        args.put(DESCRIPTION, film.description)
        args.put(RELEASEDATE, film.releaseDate)
        args.put(RATING, film.rating)
        args.put(GENRE, film.genre)
        args.put(POPULARITY, film.popularity)
        if(movie){
            contexts.contentResolver.update(CONTENT_URI_MOVIE,args,null,null)
        }else{
            contexts.contentResolver.update(CONTENT_URI_TV_SHOW,args,null,null)
        }
        return database!!.update(DATABASE_TABLE, args, BaseColumns._ID + "= '" + film.id + "'", null)
    }

    fun updateFilm(film: ArrayList<Film>) {
        deleteAll()
        for(dataFilm in film) {
            insert(dataFilm)
        }
    }
    /**
     * Gunakan method ini untuk query delete
     *
     * @param id id yang akan di delete
     * @return int jumlah row yang di delete
     */
    fun delete(id: Int): Int {
        return database!!.delete(DATABASE_TABLE, "${BaseColumns._ID} = '$id'", null)
    }

    fun deleteAll() {
        if(movie){
            contexts.contentResolver.delete(CONTENT_URI_MOVIE,null,null)
        }else{
            contexts.contentResolver.delete(CONTENT_URI_TV_SHOW,null,null)
        }
        database!!.delete(DATABASE_TABLE, null,null)
    }
    /*
    METHOD DI BAWAH INI ADALAH QUERY UNTUK CONTENT PROVIDER
    NILAI BALIK CURSOR
    */

    /**
     * Ambil data dari note berdasarakan parameter id
     * Gunakan method ini untuk ambil data di dalam provider
     *
     * @param id id note yang dicari
     * @return cursor hasil query
     */
    fun queryByIdProvider(id: String): Cursor {
        return database!!.query(DATABASE_TABLE, null, "${BaseColumns._ID} = ?", arrayOf(id), null, null, null, null)
    }

    /**
     * Ambil data dari semua note yang ada di dalam database
     * Gunakan method ini untuk ambil data di dalam provider
     *
     * @return cursor hasil query
     */
    fun queryProvider(): Cursor {
        return database!!.query(DATABASE_TABLE, null, null, null, null, null, "${BaseColumns._ID} ASC")
    }

    fun insertProvider(values: ContentValues): Long {
        return database!!.insert(DATABASE_TABLE, null, values)
    }

    fun updateProvider(id: String, values: ContentValues): Int {
        return database!!.update(DATABASE_TABLE, values, "${BaseColumns._ID} = ?", arrayOf(id))
    }

    fun deleteProvider(id: String): Int {
        return database!!.delete(DATABASE_TABLE, "${BaseColumns._ID} = ?", arrayOf(id))
    }

    companion object {
        private var DATABASE_TABLE=TABLE_MOVIE
        private var INSTANCE: MovieHelper? = null

        fun getInstance(context: Context,tableMovie: Boolean): MovieHelper? {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MovieHelper(context,tableMovie)
                    }
                }
            }
            return INSTANCE
        }
    }
}
