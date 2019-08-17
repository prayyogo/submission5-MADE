package com.dicoding.prayogo.movieapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
): Parcelable