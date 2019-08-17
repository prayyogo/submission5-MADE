package com.dicoding.prayogo.movieapp.api

import com.dicoding.prayogo.movieapp.BuildConfig
import com.dicoding.prayogo.movieapp.MainActivity
import com.dicoding.prayogo.movieapp.model.Genres
import com.dicoding.prayogo.movieapp.model.MovieDb
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiMovieDb {
    @GET("movie/now_playing")
    fun getNowPlayingMovie(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = MainActivity.LANGUAGE_FILM.toString(),
        @Query("page") page: Int
    ): Observable<MovieDb>

    @GET("tv/on_the_air")
    fun getOnTheAirTVShow(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String =  MainActivity.LANGUAGE_FILM.toString(),
        @Query("page") page: Int
    ): Observable<MovieDb>

    @GET("discover/movie")
    fun getReleaseMovie(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("primary_release_date.gte") releaseDateGte: String = MainActivity.TODAY.toString(),
        @Query("primary_release_date.lte") releaseDateLte: String = MainActivity.TODAY.toString()
    ): Observable<MovieDb>

    @GET("search/movie")
    fun getSearchMovie(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = MainActivity.LANGUAGE_FILM.toString(),
        @Query("query") searchFilm: String = MainActivity.SEARCH_FILM.toString(),
        @Query("page") page: Int
    ): Observable<MovieDb>

    @GET("search/tv")
    fun getSearchTVShow(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = MainActivity.LANGUAGE_FILM.toString(),
        @Query("query") searchFilm: String = MainActivity.SEARCH_FILM.toString(),
        @Query("page") page: Int
    ): Observable<MovieDb>

    @GET("genre/movie/list")
    fun getGenres(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = MainActivity.LANGUAGE_FILM.toString()
    ): Observable<Genres>
}