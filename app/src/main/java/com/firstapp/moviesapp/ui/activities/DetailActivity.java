package com.firstapp.moviesapp.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.adapters.CompanyAdapter;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.models.Genre;
import com.firstapp.moviesapp.models.MovieDetail;
import com.firstapp.moviesapp.utils.Constants;
import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class DetailActivity extends AppCompatActivity {

    TextView voteAverage;
    TextView runtime;
    FlexboxLayout genresView;
    RecyclerView rcvCompanies;
    TextView overview;
    ImageView thumbnail;
    TextView title;
    @Inject
    MoviesApi moviesApi;
    int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        assert extras != null : "Extras is null!";
        setContentView(R.layout.activity_detail);
        voteAverage = findViewById(R.id.tvVoteAverage);
        title = findViewById(R.id.tvTitle);
        overview = findViewById(R.id.tvOverview);
        thumbnail = findViewById(R.id.ivBackdrop);
        runtime = findViewById(R.id.tvRuntime);
        genresView = findViewById(R.id.genres);
        rcvCompanies = findViewById(R.id.rcvCompanies);
        movieId = extras.getInt(Constants.MOVIE_ID);
        rcvCompanies.setLayoutManager(new LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        ));

        setMovieDetailInfo();
        overview.setText(extras.getString(Constants.OVERVIEW));
        title.setText(extras.getString(Constants.TITLE));
        Glide.with(this)
            .load(
                Constants.IMAGE_HOST_URL + extras.getString(Constants.BACKDROP_IMAGE_URL)
            ).into(thumbnail);
        // set vote average
        DecimalFormat pattern = new DecimalFormat("#.#");
        pattern.setRoundingMode(RoundingMode.HALF_UP);
        voteAverage.setText(pattern.format(
            extras.getDouble(Constants.VOTE_AVERAGE)
        ));
    }

    public void addToFavouriteMovies(View view) {

    }

    @SuppressLint("SetTextI18n")
    void setMovieDetailInfo() {
        Call<MovieDetail> req = moviesApi.getMovieDetail(movieId);
        new Thread(() -> {
            try {
                Response<MovieDetail> res = req.execute();
                MovieDetail info = res.body();
                assert info != null;
                runOnUiThread(() -> {
                    runtime.setText(info.runtime + " min");
                    renderGenres(info.genres);
                    rcvCompanies.setAdapter(new CompanyAdapter(info.production_companies));
                });
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    void renderGenres(List<Genre> genres) {
        genres.forEach(genre -> {
            View view = LayoutInflater.from(DetailActivity.this).inflate(
                R.layout.view_movie_genre,
                new FlexboxLayout(DetailActivity.this),
                false
            );
            ((TextView) view.findViewById(R.id.tvGenre)).setText(genre.name);
            genresView.addView(view);
        });
    }

    public void back(View view) {
        finish();
    }
}