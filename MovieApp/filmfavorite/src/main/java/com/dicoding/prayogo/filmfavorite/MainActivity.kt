package com.dicoding.prayogo.filmfavorite

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.dicoding.prayogo.filmfavorite.MappingHelper.Companion.mapCursorToArrayList
import com.dicoding.prayogo.filmfavorite.adapter.FilmAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import kotlin.properties.Delegates
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import com.dicoding.prayogo.filmfavorite.DatabaseContract.NoteColumns.Companion.CONTENT_URI_MOVIE
import com.dicoding.prayogo.filmfavorite.DatabaseContract.NoteColumns.Companion.NAME
import com.dicoding.prayogo.filmfavorite.model.Film


class MainActivity : AppCompatActivity(), LoadFilmCallback, LoaderManager.LoaderCallbacks<Cursor>  {
    private var cursorLoader:CursorLoader?=null
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        cursorLoader= CursorLoader(this, Uri.parse("content://com.dicoding.prayogo.movieapp/movie/id"),null,null,null,null)
        Log.d("test",cursorLoader.toString())
        return cursorLoader as CursorLoader
    }

    override fun onLoadFinished(p0: Loader<Cursor>, cursor: Cursor?) {
        Log.d("test","finish")
        if(cursor!=null){
            Log.d("test",cursor.count.toString())
        }
    }

    override fun onLoaderReset(p0: Loader<Cursor>) {
    }

    private var adapterMovie by Delegates.notNull<FilmAdapter>()
    private var myObserver: DataObserver? = null
    private var datamovie=ArrayList<Film>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // adapterMovie = FilmAdapter(applicationContext)
       // rv_movie.layoutManager = LinearLayoutManager(applicationContext)
       // rv_movie.adapter= adapterMovie

        val myUri = Uri.parse(CONTENT_URI_MOVIE.toString())
        val cr:ContentResolver=contentResolver
        val youcp=cr.acquireContentProviderClient(myUri)
        var cursor=youcp.query(myUri,null,null,null,"${BaseColumns._ID} DESC", null)
      //  val word = cursor.getColumnIndex(NAME)

        if (cursor.moveToFirst()) {
            do {
                val word = cursor.getColumnIndex(NAME)
                Log.d("test",word.toString())
            } while (cursor.moveToNext())

        } else {
            Toast.makeText(this@MainActivity, "Nothing is inside the cursor ", Toast.LENGTH_LONG).show()
        }
      //  cursor.close()
       // getData(this, this).execute()
       cursor= contentResolver.query(CONTENT_URI_MOVIE,null,null,null,null)
        Log.d("test",cursor.count.toString())
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }
   override fun postExecute(notes: Cursor) {

        val listNotes = mapCursorToArrayList(notes)
        if (listNotes.size > 0) {
            //consumerAdapter.setListNotes(listNotes)
        } else {
            Toast.makeText(this, "Tidak Ada data saat ini", Toast.LENGTH_SHORT).show()
          //  consumerAdapter.setListNotes(ArrayList<NoteItem>())
        }
    }

     class getData  constructor(context: Context, callback: LoadFilmCallback) :
        AsyncTask<Void, Void, Cursor>() {
        private val weakContext: WeakReference<Context> = WeakReference(context)
         private val weakCallback: WeakReference<LoadFilmCallback> = WeakReference<LoadFilmCallback>(callback)


         override fun doInBackground(vararg voids: Void): Cursor? {
            return weakContext.get()?.contentResolver?.query(CONTENT_URI_MOVIE, null, null, null, null)
        }

        override fun onPostExecute(data: Cursor) {
            super.onPostExecute(data)
            weakCallback.get()?.postExecute(data)
        }

    }
    internal class DataObserver(handler: Handler, val context: Context) : ContentObserver(handler) {

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            getData(context, context as MainActivity).execute()
        }
    }
}
