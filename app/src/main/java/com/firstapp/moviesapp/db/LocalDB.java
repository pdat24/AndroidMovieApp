package com.firstapp.moviesapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.firstapp.moviesapp.models.FavouriteMovie;
import com.firstapp.moviesapp.models.MovieDetail;

import java.util.concurrent.atomic.AtomicReference;

@Database(entities = {FavouriteMovie.class}, exportSchema = false, version = 1)
public abstract class LocalDB extends RoomDatabase {
    abstract FavouriteMoviesDao getFavouriteMoviesDao();

    static AtomicReference<LocalDB> _instance = null;

    static public LocalDB getInstance(Context context) {
        if (_instance == null) {
            synchronized (LocalDB.class) {
                _instance = new AtomicReference<>(
                    new RoomDatabase.Builder<>(
                        context,
                        LocalDB.class,
                        "favourite_movies.db"
                    ).build()
                );
            }
        }
        return _instance.get();
    }
}
