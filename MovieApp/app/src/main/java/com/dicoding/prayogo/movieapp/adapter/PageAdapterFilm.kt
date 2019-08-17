package com.dicoding.prayogo.movieapp.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dicoding.prayogo.movieapp.MoviesFragment
import com.dicoding.prayogo.movieapp.R
import com.dicoding.prayogo.movieapp.TVShowsFragment

class PageAdapterFilm (fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm){

    private val pages = listOf(
        MoviesFragment(),
        TVShowsFragment()
    )

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> context.resources?.getString(R.string.movies)
            else -> context.resources?.getString(R.string.tv_shows)
        }.toString()
    }
}