package com.firstapp.moviesapp.ui.fragments;

import static com.firstapp.moviesapp.utils.Constants.DEFAULT_GENRE_ID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.firstapp.moviesapp.MainApp;
import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.adapters.BannerAdapter;
import com.firstapp.moviesapp.adapters.FilteredGenreAdapter;
import com.firstapp.moviesapp.adapters.MovieAdapter;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.models.Category;
import com.firstapp.moviesapp.models.Genre;
import com.firstapp.moviesapp.models.TrendingMovie;
import com.firstapp.moviesapp.models.UpcomingMovie;
import com.firstapp.moviesapp.ui.activities.MainActivity;
import com.firstapp.moviesapp.ui.activities.SearchActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreFragment extends Fragment {
    RecyclerView rcvTrendingMovies;
    RecyclerView rcvUpcomingMovies;
    MaterialCardView btnSearch;
    RecyclerView rcvCategory;
    RecyclerView rcvRow1;
    RecyclerView rcvRow2;
    RecyclerView rcvRow3;
    TextView genre;
    ViewPager2 viewPager2;
    MoviesApi moviesApi;
    CircularProgressIndicator loadingGenres;
    CircularProgressIndicator loadingTrendingMovies;
    CircularProgressIndicator loadingUpcomingMovies;
    LinearLayout indicatorContainer;
    CircularProgressIndicator loadingRecommendationMovies;
    AtomicInteger tmp = new AtomicInteger(0);
    MutableLiveData<Integer> loadedRowNumber = new MutableLiveData<>(0);
    MutableLiveData<Genre> filteredGenre = new MutableLiveData<>(
        new Genre(DEFAULT_GENRE_ID, "")
    );

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
        viewPager2 = view.findViewById(R.id.slider);
        btnSearch = view.findViewById(R.id.btnSearch);
        loadingGenres = view.findViewById(R.id.loadingGenres);
        indicatorContainer = view.findViewById(R.id.indicatorContainer);
        genre = view.findViewById(R.id.tvGenre);
        loadingTrendingMovies = view.findViewById(R.id.loadingTrendingMovies);
        loadingUpcomingMovies = view.findViewById(R.id.loadingUpcomingMovies);
        loadingRecommendationMovies = view.findViewById(R.id.loadingRecommendation);
        moviesApi = ((MainActivity) requireActivity()).moviesApi;

        assignHorizontalLinearLayoutManager(rcvCategory);
        assignHorizontalLinearLayoutManager(rcvUpcomingMovies);
        assignHorizontalLinearLayoutManager(rcvTrendingMovies);
        assignHorizontalLinearLayoutManager(rcvRow1);
        assignHorizontalLinearLayoutManager(rcvRow2);
        assignHorizontalLinearLayoutManager(rcvRow3);
        if (MainActivity.trendingMovie == null) {
            loadingTrendingMovies.setVisibility(View.VISIBLE);
        }
        if (MainActivity.movieCategory == null) {
            loadingGenres.setVisibility(View.VISIBLE);
        }
        if (MainActivity.movieCategory == null) {
            loadingUpcomingMovies.setVisibility(View.VISIBLE);
        }
        if (
            MainActivity.row1Movies == null ||
                MainActivity.row2Movies == null ||
                MainActivity.row3Movies == null
        ) {
            loadingRecommendationMovies.setVisibility(View.VISIBLE);
        }

        renderCarousel();
        loadedRowNumber.observe(requireActivity(), value -> {
            if (value >= 3) {
                loadingRecommendationMovies.setVisibility(View.GONE);
            }
        });
        btnSearch.setOnClickListener((v) ->
            startActivity(new Intent(requireActivity(), SearchActivity.class))
        );
        handleFilterGenres();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTrendingMovies();
        loadUpcomingMovies();
        loadGenres();
    }

    void assignHorizontalLinearLayoutManager(@NonNull RecyclerView view) {
        view.setLayoutManager(
            new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        );
    }

    void renderCarousel() {
        viewPager2.setAdapter(
            new BannerAdapter(Arrays.asList(
                R.drawable.wide, R.drawable.wide1, R.drawable.wide3)
            )
        );

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int prevPage = 1;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicatorContainer.getChildAt(prevPage).setBackground(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.bg_unchecked_indicator
                    )
                );
                indicatorContainer.getChildAt(position).setBackground(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.bg_checked_indicator
                    )
                );
                prevPage = position;
            }
        });
        viewPager2.setCurrentItem(1);
        viewPager2.setOffscreenPageLimit(3);
        CompositePageTransformer pageTransformer = new CompositePageTransformer();
        pageTransformer.addTransformer(new MarginPageTransformer(32));
        pageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(pageTransformer);
    }

    void loadTrendingMovies() {
        if (MainActivity.trendingMovie != null) {
            loadingTrendingMovies.setVisibility(View.GONE);
            rcvTrendingMovies.setAdapter(new MovieAdapter(
                MainActivity.trendingMovie.results
            ));
        } else {
            Call<TrendingMovie> task = moviesApi.getTrendingMovie();
            new Thread(() -> {
                try {
                    Response<TrendingMovie> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        MainActivity.trendingMovie = response.body();
                        requireActivity().runOnUiThread(() -> {
                            loadingTrendingMovies.setVisibility(View.GONE);
                            rcvTrendingMovies.setAdapter(
                                new MovieAdapter(MainActivity.trendingMovie.results)
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
        if (MainActivity.movieCategory != null) {
            loadingGenres.setVisibility(View.GONE);
            rcvCategory.setAdapter(new FilteredGenreAdapter(requireActivity(), filteredGenre, MainActivity.movieCategory.genres));
        } else {
            Call<Category> task = moviesApi.getMovieGenres();
            new Thread(() -> {
                try {
                    Response<Category> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        List<Genre> genres = response.body().genres;
                        MainApp.genres = genres;
                        MainActivity.movieCategory = response.body();
                        requireActivity().runOnUiThread(() -> {
                            loadingGenres.setVisibility(View.GONE);
                            rcvCategory.setAdapter(
                                new FilteredGenreAdapter(requireActivity(), filteredGenre, genres)
                            );
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    @SuppressLint("SetTextI18n")
    void handleFilterGenres() {
        filteredGenre.observe(requireActivity(), (genre) -> {
            if (genre.id == DEFAULT_GENRE_ID) {
                this.genre.setText("(Mix)");
                loadRecommendationMovies(MainActivity.row1Movies, 3, rcvRow1, Row.ROW1);
                loadRecommendationMovies(MainActivity.row2Movies, 4, rcvRow2, Row.ROW2);
                loadRecommendationMovies(MainActivity.row3Movies, 5, rcvRow3, Row.ROW3);
            } else {
                this.genre.setText("(" + genre.name + ")");
                loadMovieByGenre(rcvRow1, String.valueOf(genre.id), 1);
                loadMovieByGenre(rcvRow2, String.valueOf(genre.id), 2);
                loadMovieByGenre(rcvRow3, String.valueOf(genre.id), 3);
            }
        });
    }

    void loadUpcomingMovies() {
        if (MainActivity.upcomingMovie != null) {
            loadingUpcomingMovies.setVisibility(View.GONE);
            rcvUpcomingMovies.setAdapter(new MovieAdapter(
                MainActivity.upcomingMovie.results
            ));
        } else {
            Call<UpcomingMovie> task = moviesApi.getUpcomingMovie(2);
            new Thread(() -> {
                try {
                    Response<UpcomingMovie> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        MainActivity.upcomingMovie = response.body();
                        requireActivity().runOnUiThread(() -> {
                            loadingUpcomingMovies.setVisibility(View.GONE);
                            rcvUpcomingMovies.setAdapter(
                                new MovieAdapter(MainActivity.upcomingMovie.results)
                            );
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    void loadRecommendationMovies(
        TrendingMovie cachedMovies,
        int page,
        RecyclerView view,
        Row row
    ) {
        if (cachedMovies != null) {
            loadedRowNumber.postValue(tmp.addAndGet(1));
            view.setAdapter(new MovieAdapter(cachedMovies.results));
        } else {
            Call<TrendingMovie> task = moviesApi.getMovies(page);
            new Thread(() -> {
                try {
                    Response<TrendingMovie> response = task.execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (row == Row.ROW1) {
                            MainActivity.row1Movies = response.body();
                        } else if (row == Row.ROW2) {
                            MainActivity.row2Movies = response.body();
                        } else if (row == Row.ROW3) {
                            MainActivity.row3Movies = response.body();
                        }
                        requireActivity().runOnUiThread(() -> {
                            loadedRowNumber.postValue(tmp.addAndGet(1));
                            view.setAdapter(
                                new MovieAdapter(response.body().results)
                            );
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    void loadMovieByGenre(RecyclerView view, String genreId, int page) {
        Call<TrendingMovie> task = moviesApi.getMoviesByGenre(genreId, page);
        new Thread(() -> {
            try {
                Response<TrendingMovie> response = task.execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    requireActivity().runOnUiThread(() -> {
                        loadedRowNumber.postValue(tmp.addAndGet(1));
                        view.setAdapter(
                            new MovieAdapter(response.body().results)
                        );
                    });
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    enum Row {
        ROW1, ROW2, ROW3
    }
}