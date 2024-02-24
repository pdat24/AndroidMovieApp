package com.firstapp.moviesapp.ui.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.db.FavouriteMoviesDaoImpl;
import com.firstapp.moviesapp.models.Category;
import com.firstapp.moviesapp.models.FavouriteMovie;
import com.firstapp.moviesapp.models.MovieDetail;
import com.firstapp.moviesapp.models.TrendingMovie;
import com.firstapp.moviesapp.models.UpcomingMovie;
import com.firstapp.moviesapp.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    FavouriteMoviesDaoImpl favouriteMoviesDao;
    @Inject
    public MoviesApi moviesApi;
    static public Category movieCategory = null;
    static public TrendingMovie trendingMovie = null;
    static public UpcomingMovie upcomingMovie = null;
    static public TrendingMovie row1Movies = null;
    static public TrendingMovie row2Movies = null;
    static public TrendingMovie row3Movies = null;
    static public List<MovieDetail> favouriteMovies = new ArrayList<>();
    static public boolean isFavouriteMoviesChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getColor(R.color.black_2));
        favouriteMoviesDao = new FavouriteMoviesDaoImpl(this);
        BottomNavigationView navBar = findViewById(R.id.bottomNavBar);

        // set up nav controller
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.bottomNavHost);
        assert host != null : "NavHost is null!";
        NavigationUI.setupWithNavController(navBar, host.getNavController());
        loadFavouriteMovies(this);
    }

    static public void loadFavouriteMovies(Context context) {
        MoviesApi moviesApi = (new Retrofit.Builder())
            .baseUrl(Constants.MOVIE_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesApi.class);
        FavouriteMoviesDaoImpl favouriteMoviesDao = new FavouriteMoviesDaoImpl(context);
        new Thread(() -> {
            List<FavouriteMovie> tmp = favouriteMoviesDao.getFavouriteMovies();
            if (tmp.size() != favouriteMovies.size()) {
                favouriteMovies.clear();
                for (FavouriteMovie movie : tmp) {
                    Call<MovieDetail> req = moviesApi.getMovieDetail(movie.id);
                    try {
                        Response<MovieDetail> res = req.execute();
                        if (res.isSuccessful()) {
                            favouriteMovies.add(res.body());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}