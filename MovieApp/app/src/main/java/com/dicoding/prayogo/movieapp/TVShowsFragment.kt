package com.dicoding.prayogo.movieapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.dicoding.prayogo.movieapp.adapter.AdapterMovieDb
import com.dicoding.prayogo.movieapp.api.ApiMovieDb
import com.dicoding.prayogo.movieapp.database.DatabaseTVShowHelper
import com.dicoding.prayogo.movieapp.database.MovieHelper
import com.dicoding.prayogo.movieapp.model.Film
import com.dicoding.prayogo.movieapp.model.Genre
import com.dicoding.prayogo.movieapp.model.Genres
import com.dicoding.prayogo.movieapp.model.MovieDb
import com.dicoding.prayogo.movieapp.model.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tvshows.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

class TVShowsFragment : Fragment() {
    companion object{
        var dataTVShowFavorite=ArrayList<Film>()
    }
    private var adapterMovieDb by Delegates.notNull<AdapterMovieDb>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()
    private var listGenreMovieDb by Delegates.notNull<ArrayList<Genre>>()
    private var resultTheMovieDb by Delegates.notNull<ArrayList<Result>>()
    private var movieDb by Delegates.notNull<MovieDb>()
    private var searchView by Delegates.notNull<SearchView>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        page = 1
        totalPage = 0
        if(savedInstanceState!=null){
            page=savedInstanceState.getInt("PageTVShow")
            resultTheMovieDb=savedInstanceState.getParcelableArrayList("ResultTVShow")
            listGenreMovieDb=savedInstanceState.getParcelableArrayList("GenreTVShow")
            totalPage=savedInstanceState.getInt("TotalPageTVShow")

            adapterMovieDb = AdapterMovieDb(
                this.activity!!,
                resultTheMovieDb,
                listGenreMovieDb,
                dataTVShowFavorite
            )
            hideLoading()
            if(page==1){
                rv_tv_shows.layoutManager = LinearLayoutManager(context)
                rv_tv_shows.adapter = adapterMovieDb
            }else{
                rv_tv_shows.layoutManager = LinearLayoutManager(context)
                rv_tv_shows.adapter = adapterMovieDb
                adapterMovieDb.refreshAdapter(resultTheMovieDb)
            }
        }else{
            doLoadDatabase()
            doLoadData()
        }
        initListener()
        //retry button
        btn_retry_tv_show.setOnClickListener {
            btn_retry_tv_show.visibility=View.GONE
            doLoadDatabase()
            doLoadData()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tvshows, container, false)
    }
    @Override
    override fun onSaveInstanceState(outState:Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("ResultTVShow",resultTheMovieDb)
        outState.putParcelableArrayList("GenreTVShow",listGenreMovieDb)
        outState.putInt("PageTVShow",page)
        outState.putInt("TotalPageTVShow",totalPage)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
        searchView = menu.findItem(R.id.action_search_menu).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint=resources.getString( R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return if(query.trim().isEmpty()){
                    doLoadDatabase()
                    doLoadData()
                    false
                } else{
                    MainActivity.SEARCH_FILM=query
                    doSearchData()
                    true
                }
            }
            override fun onQueryTextChange(query: String): Boolean {
                if(query.trim().isEmpty()){
                    doLoadDatabase()
                    doLoadData()
                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.action_search_menu -> {
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }
    private  fun  doLoadDatabase(){
        /*val dbHandler = DatabaseTVShowHelper(this.context!!, null)
        val cursor = dbHandler.getAllFilm()
        dataTVShowFavorite.clear()
        if(cursor?.count!=0){
            cursor?.moveToFirst()
            dataTVShowFavorite.add(Film(0,
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_PHOTO)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_NAME)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_DESCRIPTION)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RELEASEDATE)),
                cursor?.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RATING)),
                cursor?.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_GENRE)),
                cursor?.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_POPULARITY))))
            while (cursor?.moveToNext()!!) {
                dataTVShowFavorite.add(Film(0,
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_PHOTO)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RELEASEDATE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_RATING)),
                    cursor.getString(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_GENRE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseTVShowHelper.COLUMN_POPULARITY))))
            }
            cursor.close()
        }
        dbHandler.close()*/
        dataTVShowFavorite.clear()
        val tvShowHelper= MovieHelper(this.context!!,false)
        tvShowHelper.open()
        if (tvShowHelper.query().isNotEmpty()){
            dataTVShowFavorite.addAll(tvShowHelper.query())
        }
        tvShowHelper.close()
    }
    @SuppressLint("CheckResult")
    private fun doLoadData() {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val apiMovieDb = retrofit.create(ApiMovieDb::class.java)
        apiMovieDb.getGenres()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { genreMovieDB: Genres ->
                    listGenreMovieDb = genreMovieDB.genres as ArrayList
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_tv_show.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
        apiMovieDb.getOnTheAirTVShow(page = page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { theMovieDb: MovieDb ->
                    resultTheMovieDb = theMovieDb.results as ArrayList
                    movieDb=theMovieDb
                    if (page == 1) {
                        adapterMovieDb = AdapterMovieDb(
                            this.activity!!,
                            resultTheMovieDb,
                            listGenreMovieDb,
                            dataTVShowFavorite
                        )
                        rv_tv_shows.layoutManager = LinearLayoutManager(activity)
                        rv_tv_shows.adapter = adapterMovieDb
                    } else {
                        adapterMovieDb.refreshAdapter(resultTheMovieDb)
                    }
                    totalPage = theMovieDb.totalPages
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_tv_show.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
    }
    @SuppressLint("CheckResult")
    private fun doSearchData() {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val apiMovieDb = retrofit.create(ApiMovieDb::class.java)
        apiMovieDb.getGenres()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { genreMovieDB: Genres ->
                    listGenreMovieDb = genreMovieDB.genres as ArrayList
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_tv_show.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
        apiMovieDb.getSearchTVShow(page=page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { theMovieDb: MovieDb ->
                    resultTheMovieDb = theMovieDb.results as ArrayList
                    movieDb=theMovieDb
                    adapterMovieDb = AdapterMovieDb(
                        this.activity!!,
                        resultTheMovieDb,
                        listGenreMovieDb,
                        dataTVShowFavorite
                    )
                    rv_tv_shows.layoutManager = LinearLayoutManager(activity)
                    rv_tv_shows.adapter = adapterMovieDb
                    totalPage = movieDb.totalPages
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_tv_show.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
    }
    private fun initListener() {
        rv_tv_shows.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                if (!isLoading && isLastPosition && page < totalPage) {
                    showLoading(true)
                    page = page.plus(1)
                    doLoadData()
                }
            }
        })
    }
    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        btn_retry_tv_show.visibility=View.GONE
        progress_bar_tv_shows.visibility = View.VISIBLE
        rv_tv_shows.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_tv_shows.visibility = View.GONE
        rv_tv_shows.visibility = View.VISIBLE
    }
}
