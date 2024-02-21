package com.firstapp.moviesapp.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.apis.MoviesApi;
import com.firstapp.moviesapp.ui.viewmodels.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    public MoviesApi moviesApi;
    public MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getColor(R.color.black_2));
        mainViewModel = (new ViewModelProvider(this)).get(MainViewModel.class);
        BottomNavigationView navBar = findViewById(R.id.bottomNavBar);

        // set up nav controller
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.bottomNavHost);
        assert host != null : "NavHost is null!";
        NavigationUI.setupWithNavController(navBar, host.getNavController());
    }
}