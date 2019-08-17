package com.dicoding.prayogo.movieapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Film (
    var id:Int?=null,
    var photo:String?=null,
    var name:String?=null,
    var description:String?=null,
    var releaseDate:String?=null,
    var rating:Double?=null,
    var genre:String?=null,
    var popularity:Double?=null
): Parcelable