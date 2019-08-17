package com.dicoding.prayogo.movieapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.TextView
import com.dicoding.prayogo.movieapp.adapter.AdapterMovieFavorite
import com.dicoding.prayogo.movieapp.database.DatabaseTVShowHelper
import com.dicoding.prayogo.movieapp.database.MovieHelper
import com.dicoding.prayogo.movieapp.model.Film
import kotlinx.android.synthetic.main.fragment_tvshows_favorite.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class TVShowsFavoriteFragment : Fragment() {

    companion object{
        var dataTVShow=ArrayList<Film>()
        var noDataText by Delegates.notNull<TextView>()
    }
    private var adapterMovieDb by Delegates.notNull<AdapterMovieFavorite>()
    private var tempDataTVShow=ArrayList<Film>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var searchView by Delegates.notNull<SearchView>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        noDataText=tv_no_data_tv_show
        if(savedInstanceState!=null) {
            hideLoading()
            dataTVShow = savedInstanceState.getParcelableArrayList("TVShow")
            if(dataTVShow.size!=0){
                adapterMovieDb = AdapterMovieFavorite(this.activity!!,
                    dataTVShow,false
                )
                tempDataTVShow.addAll(dataTVShow)
                rv_tv_shows_favorite.layoutManager = LinearLayoutManager(activity)
                rv_tv_shows_favorite.adapter = adapterMovieDb
                tv_no_data_tv_show.visibility=View.GONE
            }else{
                tv_no_data_tv_show.visibility=View.VISIBLE
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
        return inflater.inflate(R.layout.fragment_tvshows_favorite, container, false)
    }
    @Override
    override fun onSaveInstanceState(outState:Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("TVShow",dataTVShow)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.favorite_menu, menu)
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint=resources.getString( R.string.search)
        adapterMovieDb = AdapterMovieFavorite(this.activity!!,
            dataTVShow,false
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(query: String): Boolean {
                return if(query.trim().isEmpty()){
                    adapterMovieDb.setFilterTVShow(tempDataTVShow)
                    false
                }else{
                    val filteredList = ArrayList<Film>()
                    for (row in tempDataTVShow) {
                        if (row.name!!.toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    if(filteredList.isEmpty()){
                        tv_no_data_tv_show.visibility=View.VISIBLE
                    }else{
                        tv_no_data_tv_show.visibility=View.GONE
                    }
                    adapterMovieDb.setFilterTVShow(filteredList)
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
        dataTVShow.clear()
        tv_no_data_tv_show.visibility=View.GONE
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
    private  fun  doLoadDatabase(){
       /* val dbHandler = DatabaseTVShowHelper(this.context!!, null)
        val cursor = dbHandler.getAllFilm()
        if(cursor?.count!=0){
            cursor?.moveToFirst()
            dataTVShow.add(Film(0,
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_PHOTO)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_NAME)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_DESCRIPTION)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RELEASEDATE)),
                cursor?.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RATING)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_GENRE)),
                cursor?.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_POPULARITY))))
            while (cursor?.moveToNext()!!) {
                dataTVShow.add(Film(0,
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_PHOTO)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RELEASEDATE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RATING)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_GENRE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_POPULARITY))))
            }
            cursor.close()
            adapterMovieDb = AdapterMovieFavorite(this.activity!!,
                dataTVShow,false
            )
            tempDataTVShow.addAll(dataTVShow)
            rv_tv_shows_favorite.layoutManager = LinearLayoutManager(activity)
            rv_tv_shows_favorite.adapter = adapterMovieDb
            tv_no_data_tv_show.visibility=View.GONE
        }else{
            tv_no_data_tv_show.visibility=View.VISIBLE
        }
        dbHandler.close()*/
        val tvShowHelper= MovieHelper(this.context!!,false)
        tvShowHelper.open()
        if (tvShowHelper.query().isNotEmpty()){
            dataTVShow.addAll(tvShowHelper.query())
            adapterMovieDb = AdapterMovieFavorite(this.activity!!,
                dataTVShow,false
            )
            tempDataTVShow.addAll(dataTVShow)
            rv_tv_shows_favorite.layoutManager = LinearLayoutManager(activity)
            rv_tv_shows_favorite.adapter = adapterMovieDb
            tv_no_data_tv_show.visibility=View.GONE
        }else{
            tv_no_data_tv_show.visibility=View.VISIBLE
        }
        tvShowHelper.close()
        hideLoading()
    }

    @SuppressLint("CheckResult")
    private fun initListener() {
        rv_tv_shows_favorite.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
               // val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            }
        })
    }

    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        progress_bar_tv_shows_favorite.visibility = View.VISIBLE
        rv_tv_shows_favorite.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_tv_shows_favorite.visibility = View.GONE
        rv_tv_shows_favorite.visibility = View.VISIBLE
    }
}
