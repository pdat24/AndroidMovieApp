package com.firstapp.moviesapp.ui.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.adapters.SearchResultAdapter;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.models.Movie;
import com.firstapp.moviesapp.models.TrendingMovie;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {
    RecyclerView rcvSearchSuggestions;
    TextInputEditText searchInput;
    InputMethodManager inputMethodManager;
    Thread searchThread = null;
    @Inject
    MoviesApi moviesApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setStatusBarColor(getColor(R.color.black_2));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        rcvSearchSuggestions = findViewById(R.id.rcvSearchSuggestions);
        searchInput = findViewById(R.id.ipSearch);

        rcvSearchSuggestions.setLayoutManager(new LinearLayoutManager(this));
        // focus on search input
        searchInput.requestFocus();
        inputMethodManager.showSoftInput(searchInput, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void onStart() {
        super.onStart();
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.trim().isEmpty())
                    handleSearchMovie(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    void handleSearchMovie(String query) {
        // TODO: Change logic
        if (searchThread != null)
            searchThread.stop();
        searchThread = new Thread(() -> {
            Call<TrendingMovie> req = moviesApi.searchMovies(query, 1);
            try {
                Response<TrendingMovie> res = req.execute();
                if (res.isSuccessful()) {
                    assert res.body() != null;
                    runOnUiThread(() -> {
                        List<Movie> results = new ArrayList<>();
                        List<Movie> tmp = res.body().results;
                        for (Movie movie : tmp)
                            if (movie.backdrop_path != null)
                                results.add(movie);
                        rcvSearchSuggestions.setAdapter(
                            new SearchResultAdapter(results)
                        );
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        searchThread.start();
    }

    public void back(View view) {
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View focusedView = getCurrentFocus();
            if (focusedView instanceof TextInputEditText) {
                Rect rect = new Rect();
                focusedView.getClipBounds(rect);
                if (!rect.contains((int) ev.getX(), (int) ev.getY())) {
                    focusedView.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(
                        focusedView.getWindowToken(),
                        InputMethodManager.RESULT_HIDDEN
                    );
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}