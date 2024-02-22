package com.firstapp.moviesapp.db;

import android.content.Context;

import com.firstapp.moviesapp.models.FavouriteMovie;
import com.firstapp.moviesapp.models.MovieDetail;

import java.util.List;

public class FavouriteMoviesDaoImpl {
    LocalDB db;
    FavouriteMoviesDao dao;

    public FavouriteMoviesDaoImpl(Context context) {
        db = LocalDB.getInstance(context);
        dao = db.getFavouriteMoviesDao();
    }

    public List<FavouriteMovie> getFavouriteMovies() {
        return dao.getMovies();
    }

    public void addFavouriteMovies(FavouriteMovie movie) {
        dao.addMovie(movie);
    }

    public void removeFavouriteMovies(int id) {
        dao.removeMovie(id);
    }
}
