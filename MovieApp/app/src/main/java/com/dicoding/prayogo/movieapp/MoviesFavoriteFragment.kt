package com.dicoding.prayogo.movieapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.dicoding.prayogo.movieapp.adapter.AdapterMovieFavorite
import com.dicoding.prayogo.movieapp.model.*
import kotlinx.android.synthetic.main.fragment_movies_favorite.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.CONTENT_URI_MOVIE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.GENRE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.NAME
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.PHOTO
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.POPULARITY
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.RATING
import com.dicoding.prayogo.movieapp.database.DatabaseContract.NoteColumns.Companion.RELEASEDATE
import com.dicoding.prayogo.movieapp.database.MovieHelper

class MoviesFavoriteFragment : Fragment() {

    companion object{
        var dataMovie=ArrayList<Film>()
        var noDataText by Delegates.notNull<TextView>()
    }
    private var adapterMovieDb by Delegates.notNull<AdapterMovieFavorite>()
    private var tempDataMovie=ArrayList<Film>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var searchView by Delegates.notNull<SearchView>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        noDataText=tv_no_data_movie
        if(savedInstanceState!=null) {
            hideLoading()
            dataMovie = savedInstanceState.getParcelableArrayList("Movie")
            if(dataMovie.size!=0){
                adapterMovieDb = AdapterMovieFavorite(this.activity!!,
                    dataMovie,true
                )
                tempDataMovie.addAll(dataMovie)
                rv_movie_favorite.layoutManager = LinearLayoutManager(activity)
                rv_movie_favorite.adapter = adapterMovieDb
                tv_no_data_movie.visibility=View.GONE
            }else{
                tv_no_data_movie.visibility=View.VISIBLE
            }
        }else{
            loadDatabase()
        }
        initListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_movies_favorite, container, false)
    }
    @Override
    override fun onSaveInstanceState(outState:Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("Movie",dataMovie)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.favorite_menu, menu)
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint=resources.getString( R.string.search)
        adapterMovieDb = AdapterMovieFavorite(this.activity!!,
            dataMovie,true
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(query: String): Boolean {
                return if(query.trim().isEmpty()){
                    adapterMovieDb.setFilterMovie(tempDataMovie)
                    false
                }else{
                    val filteredList = ArrayList<Film>()
                    for (row in tempDataMovie) {
                        if (row.name!!.toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    if(filteredList.isEmpty()){
                        tv_no_data_movie.visibility=View.VISIBLE
                    }else{
                        tv_no_data_movie.visibility=View.GONE
                    }
                    adapterMovieDb.setFilterMovie(filteredList)
                    true
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.action_search -> {
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    private fun loadDatabase(){
        showLoading(true)
        dataMovie.clear()
        tv_no_data_movie.visibility=View.GONE
        Thread(Runnable {
            // performing some dummy time taking operation
            var i=0
            while(i<Int.MAX_VALUE){
                i++
            }
            // try to touch View of UI thread
            activity?.runOnUiThread {
                doLoadDatabase()
            }
        }).start()

    }
    @SuppressLint("CheckResult")
    private  fun  doLoadDatabase(){
        val cursor = activity?.contentResolver?.query(CONTENT_URI_MOVIE, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
                dataMovie.add( Film(0,
                cursor.getString(cursor.getColumnIndex(PHOTO)),
                cursor.getString(cursor.getColumnIndex(NAME)),
                cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(RELEASEDATE)),
                cursor.getDouble(cursor.getColumnIndex(RATING)),
                cursor.getString(cursor.getColumnIndex(GENRE)),
                cursor.getDouble(cursor.getColumnIndex(POPULARITY))))
            while (cursor.moveToNext()) {
                dataMovie.add( Film(0,
                    cursor.getString(cursor.getColumnIndex(PHOTO)),
                    cursor.getString(cursor.getColumnIndex(NAME)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(RELEASEDATE)),
                    cursor.getDouble(cursor.getColumnIndex(RATING)),
                    cursor.getString(cursor.getColumnIndex(GENRE)),
                    cursor.getDouble(cursor.getColumnIndex(POPULARITY))))
            }
            cursor.close()
            adapterMovieDb = AdapterMovieFavorite(this.activity!!,
                dataMovie,true
            )
            tempDataMovie.addAll(dataMovie)
            rv_movie_favorite.layoutManager = LinearLayoutManager(activity)
            rv_movie_favorite.adapter = adapterMovieDb
            tv_no_data_movie.visibility=View.GONE
        }else{
            tv_no_data_movie.visibility=View.VISIBLE
        }
        /*
        val movieHelper=MovieHelper(this.context!!,true)
        movieHelper.open()

        if (movieHelper.query().isNotEmpty()){
            dataMovie.addAll(movieHelper.query())
            adapterMovieDb = AdapterMovieFavorite(this.activity!!,
                dataMovie,true
            )
            tempDataMovie.addAll(dataMovie)
            rv_movie_favorite.layoutManager = LinearLayoutManager(activity)
            rv_movie_favorite.adapter = adapterMovieDb
            tv_no_data_movie.visibility=View.GONE
        }else{
            tv_no_data_movie.visibility=View.VISIBLE
        }
        movieHelper.close()
*/
        hideLoading()
    }

    private fun initListener() {
        rv_movie_favorite.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            }
        })
    }

    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        progress_bar_movie_favorite.visibility = View.VISIBLE
        rv_movie_favorite.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_movie_favorite.visibility = View.GONE
        rv_movie_favorite.visibility = View.VISIBLE
    }
}
