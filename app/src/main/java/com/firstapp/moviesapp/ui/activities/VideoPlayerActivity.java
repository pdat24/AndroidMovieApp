package com.firstapp.moviesapp.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.firstapp.moviesapp.R;

public class VideoPlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        playerView = findViewById(R.id.videoPlayer);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(
            "https://firebasestorage.googleapis.com/v0/b/androidmoviesapp-5484f.appspot.com/o/temp.mp4?alt=media&token=7ff69786-5a00-486d-8785-e1f46c49eb49"
        ));
        playerView.setFullscreenButtonClickListener(isFullScreen -> {
            int orientation;
            if (isFullScreen)
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            else
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            setRequestedOrientation(orientation);
        });
        player.prepare();
        player.play();
    }

    @Override
    protected void onStart() {
        super.onStart();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}