package com.firstapp.moviesapp.ui.viewmodels;

import androidx.lifecycle.ViewModel;

import com.firstapp.moviesapp.models.Category;
import com.firstapp.moviesapp.models.TrendingMovie;
import com.firstapp.moviesapp.models.UpcomingMovie;

public class MainViewModel extends ViewModel {
    public Category movieCategory = null;
    public TrendingMovie trendingMovie = null;
    public UpcomingMovie upcomingMovie = null;
    public TrendingMovie row1Movies = null;
    public TrendingMovie row2Movies = null;
    public TrendingMovie row3Movies = null;
}
