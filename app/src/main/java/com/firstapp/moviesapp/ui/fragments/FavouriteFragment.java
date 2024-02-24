package com.firstapp.moviesapp.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.moviesapp.MainApp;
import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.adapters.FavouriteMoviesAdapter;
import com.firstapp.moviesapp.adapters.FilteredGenreAdapter;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.db.FavouriteMoviesDaoImpl;
import com.firstapp.moviesapp.models.Genre;
import com.firstapp.moviesapp.models.MovieDetail;
import com.firstapp.moviesapp.ui.activities.MainActivity;
import com.firstapp.moviesapp.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {
    RecyclerView rcvGenres;
    RecyclerView rcvMovies;
    TextInputEditText searchInput;
    FavouriteMoviesDaoImpl favouriteMoviesDao;
    TextView noResult;
    TextView linkToExploreFrag;
    View emptyMovies;
    MoviesApi moviesApi;
    MutableLiveData<Genre> filteredGenre = new MutableLiveData<>(
        new Genre(Constants.DEFAULT_GENRE_ID, "")
    );
    MutableLiveData<List<MovieDetail>> showingMovies = new MutableLiveData<>();
    List<MovieDetail> favouriteMovies = MainActivity.favouriteMovies;
    List<MovieDetail> moviesBeforeSearching = new ArrayList<>();

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvGenres = view.findViewById(R.id.rcvGenres);
        rcvMovies = view.findViewById(R.id.rcvMovies);
        emptyMovies = view.findViewById(R.id.emptyMovies);
        linkToExploreFrag = view.findViewById(R.id.toExploreFrag);
        searchInput = view.findViewById(R.id.ipSearch);
        noResult = view.findViewById(R.id.tvNoResult);
        moviesApi = ((MainActivity) requireActivity()).moviesApi;
        favouriteMoviesDao = new FavouriteMoviesDaoImpl(requireContext());

        rcvGenres.setLayoutManager(new LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        ));
        rcvMovies.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (MainApp.genres != null) {
            rcvGenres.setAdapter(
                new FilteredGenreAdapter(this, filteredGenre, MainApp.genres)
            );
        }
        linkToExploreFrag.setOnClickListener((v) -> navigateToExploreFrag());
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleSearchMovies(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        observeFilteredGenreChanges();
        observeShowingMoviesChanges();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.isFavouriteMoviesChanged) {
            showingMovies.postValue(favouriteMovies);
            MainActivity.isFavouriteMoviesChanged = false;
        }
    }

    void handleSearchMovies(String query) {
        if (!query.isEmpty()) {
            List<MovieDetail> filteredMovies = new ArrayList<>();
            for (MovieDetail movie : moviesBeforeSearching) {
                if (movie.title.toLowerCase().contains(query.toLowerCase()))
                    filteredMovies.add(movie);
            }
            showingMovies.postValue(filteredMovies);
        } else
            showingMovies.postValue(moviesBeforeSearching);
    }

    void observeFilteredGenreChanges() {
        filteredGenre.observe(this, (genre) -> {
            if (genre.id == Constants.DEFAULT_GENRE_ID) {
                showingMovies.postValue(favouriteMovies);
                moviesBeforeSearching = favouriteMovies;
            } else {
                List<MovieDetail> filteredMovies = new ArrayList<>();
                for (MovieDetail movie : favouriteMovies) {
                    for (Genre g : movie.genres) {
                        if (g.id == genre.id)
                            filteredMovies.add(movie);
                    }
                }
                moviesBeforeSearching = filteredMovies;
                showingMovies.postValue(filteredMovies);
            }
        });
    }

    void navigateToExploreFrag() {
        Navigation.findNavController(requireActivity(), R.id.bottomNavHost)
            .navigate(R.id.action_favouriteFragment_to_exploreFragment);
    }

    void observeShowingMoviesChanges() {
        showingMovies.observe(this, (movies -> {
            rcvMovies.setAdapter(new FavouriteMoviesAdapter(movies));
            if (movies.isEmpty() && !favouriteMovies.isEmpty())
                noResult.setVisibility(View.VISIBLE);
            else
                noResult.setVisibility(View.GONE);
        }));
    }
}