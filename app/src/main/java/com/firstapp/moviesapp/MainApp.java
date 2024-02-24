package com.firstapp.moviesapp;

import android.app.Application;

import com.firstapp.moviesapp.models.Genre;

import java.util.List;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApp extends Application {
    static public List<Genre> genres = null;
}
