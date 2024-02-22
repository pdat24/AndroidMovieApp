package com.firstapp.moviesapp.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import com.firstapp.moviesapp.models.FavouriteMovie;

import java.util.List;

@Dao
public interface FavouriteMoviesDao {
    @Query("SELECT * FROM favourite_movies")
    List<FavouriteMovie> getMovies();

    @Upsert
    void addMovie(FavouriteMovie movie);

    @Query("DELETE FROM favourite_movies WHERE id=:movieId")
    void removeMovie(int movieId);
}
