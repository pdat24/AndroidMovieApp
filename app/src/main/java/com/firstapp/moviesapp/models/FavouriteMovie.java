package com.firstapp.moviesapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie {

    public FavouriteMovie(int mid, String mTitle, String mPosterUrl) {
        id = mid;
        mTitle = mTitle;
        mPosterUrl = mPosterUrl;
    }

    public FavouriteMovie() {}

    @PrimaryKey
    public int id;
    public String title;
    public String posterUrl;
}
