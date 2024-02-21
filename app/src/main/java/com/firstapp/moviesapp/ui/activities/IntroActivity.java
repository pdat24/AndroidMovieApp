package com.firstapp.moviesapp.ui.activities;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.firstapp.moviesapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        // navigate to main activity if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        // get views
        ScrollView scrollView = findViewById(R.id.scrollView);
        AppCompatButton btnGetStarted = findViewById(R.id.btnGetStarted);
        TextView gradientText = findViewById(R.id.tvGradientText);
        Shader linearGradient = new LinearGradient(
            0, 0, 100, 100,
            getColor(R.color.green),
            getColor(R.color.purple),
            Shader.TileMode.CLAMP
        );
        // set text color to gradient
        gradientText.getPaint().setShader(linearGradient);
        scrollView.post(
            () -> scrollView.fullScroll(View.FOCUS_DOWN)
        );
        btnGetStarted.setOnClickListener(
            (View view) -> {
                Intent intent;
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    intent = new Intent(this, MainActivity.class);
                else
                    intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
            }
        );
    }
}