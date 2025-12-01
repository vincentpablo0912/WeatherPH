package com.example.weatherappphfinal.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherappphfinal.R;

/**
 * The initial screen shown to the user. It now serves as a simple entry point to the main app.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
            boolean onboardingComplete = prefs.getBoolean("onboardingComplete", false);

            Intent intent;
            if (onboardingComplete) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            }

            startActivity(intent);
            finish(); // Finish this activity to prevent the user from coming back to it.
        }, SPLASH_DELAY);
    }
}
