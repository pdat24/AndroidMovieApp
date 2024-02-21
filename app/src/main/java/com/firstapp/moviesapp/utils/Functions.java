package com.firstapp.moviesapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Functions {
    static public boolean isInternetConnected(Context context) {
        ConnectivityManager manager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return manager.getActiveNetworkInfo().isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }

    static public void showNoInternetPrompt(Context context) {

    }
}
