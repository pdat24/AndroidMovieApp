package com.firstapp.moviesapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.adapters.BannerAdapter;
import com.firstapp.moviesapp.adapters.GenreAdapter;
import com.firstapp.moviesapp.adapters.MovieAdapter;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.models.Category;
import com.firstapp.moviesapp.models.Genre;
import com.firstapp.moviesapp.models.TrendingMovie;
import com.firstapp.moviesapp.models.UpcomingMovie;
import com.firstapp.moviesapp.ui.activities.MainActivity;
import com.firstapp.moviesapp.ui.viewmodels.MainViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreFragment extends Fragment {
    RecyclerView rcvTrendingMovies;
    RecyclerView rcvUpcomingMovies;
    RecyclerView rcvCategory;
    RecyclerView rcvRow1;
    RecyclerView rcvRow2;
    RecyclerView rcvRow3;
    RecyclerView rcvRow4;
    ViewPager2 slider;
    MoviesApi moviesApi;
    MainViewModel mainViewModel;
    CircularProgressIndicator loadingGenres;
    CircularProgressIndicator loadingTrendingMovies;
    CircularProgressIndicator loadingUpcomingMovies;
    CircularProgressIndicator loadingRecommendationMovies;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvTrendingMovies = view.findViewById(R.id.rcvBestMovies);
        rcvCategory = view.findViewById(R.id.rcvCategories);
        rcvUpcomingMovies = view.findViewById(R.id.rcvUpcomingMovies);
        rcvRow1 = view.findViewById(R.id.rcvRow1);
        rcvRow2 = view.findViewById(R.id.rcvRow2);
        rcvRow3 = view.findViewById(R.id.rcvRow3);
        rcvRow4 = view.findViewById(R.id.rcvRow4);
        slider = view.findViewById(R.id.slider);
        loadingGenres = view.findViewById(R.id.loadingGenres);
        loadingTrendingMovies = view.findViewById(R.id.loadingTrendingMovies);
        loadingUpcomingMovies = view.findViewById(R.id.loadingUpcomingMovies);
        loadingRecommendationMovies = view.findViewById(R.id.loadingRecommendation);
        moviesApi = ((MainActivity) requireActivity()).moviesApi;
        mainViewModel = ((MainActivity) requireActivity()).mainViewModel;
        rcvCategory.setLayoutManager(
            new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        );
        rcvUpcomingMovies.setLayoutManager(
            new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        );
        rcvTrendingMovies.setLayoutManager(
            new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        );
        if (mainViewModel.trendingMovie == null) {
            loadingTrendingMovies.setVisibility(View.VISIBLE);
        }
        if (mainViewModel.movieCategory == null) {
            loadingGenres.setVisibility(View.VISIBLE);
        }
        if (mainViewModel.movieCategory == null) {
            loadingUpcomingMovies.setVisibility(View.VISIBLE);
        }

        renderSlider();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTrendingMovies();
        loadUpcomingMovies();
        loadGenres();
    }

    void renderSlider() {
        slider.setAdapter(
            new BannerAdapter(Arrays.asList(
                R.drawable.wide, R.drawable.wide1, R.drawable.wide3)
            )
        );
    }

    void loadTrendingMovies() {
        if (mainViewModel.trendingMovie != null) {
            loadingTrendingMovies.setVisibility(View.GONE);
            rcvTrendingMovies.setAdapter(new MovieAdapter(
                mainViewModel.trendingMovie.results
            ));
        } else {
            Call<TrendingMovie> task = moviesApi.getTrendingMovie();
            new Thread(() -> {
                try {
                    Response<TrendingMovie> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        mainViewModel.trendingMovie = response.body();
                        requireActivity().runOnUiThread(() -> {
                            loadingTrendingMovies.setVisibility(View.GONE);
                            rcvTrendingMovies.setAdapter(
                                new MovieAdapter(mainViewModel.trendingMovie.results)
                            );
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    void loadGenres() {
        if (mainViewModel.movieCategory != null) {
            loadingGenres.setVisibility(View.GONE);
            rcvCategory.setAdapter(new GenreAdapter(mainViewModel.movieCategory.genres));
        } else {
            Call<Category> task = moviesApi.getMovieGenres();
            new Thread(() -> {
                try {
                    Response<Category> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        List<Genre> genres = response.body().genres;
                        mainViewModel.movieCategory = response.body();
                        requireActivity().runOnUiThread(() -> {
                            loadingGenres.setVisibility(View.GONE);
                            rcvCategory.setAdapter(new GenreAdapter(genres));
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    void loadUpcomingMovies() {
        if (mainViewModel.upcomingMovie != null) {
            loadingUpcomingMovies.setVisibility(View.GONE);
            rcvUpcomingMovies.setAdapter(new MovieAdapter(
                mainViewModel.upcomingMovie.results
            ));
        } else {
            Call<UpcomingMovie> task = moviesApi.getUpcomingMovie(2);
            new Thread(() -> {
                try {
                    Response<UpcomingMovie> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        mainViewModel.upcomingMovie = response.body();
                        requireActivity().runOnUiThread(() -> {
                            loadingUpcomingMovies.setVisibility(View.GONE);
                            rcvUpcomingMovies.setAdapter(
                                new MovieAdapter(mainViewModel.upcomingMovie.results)
                            );
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    void loadRecommendationMovies() {

    }
}