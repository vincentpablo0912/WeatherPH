package com.example.weatherappphfinal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherappphfinal.R;

/**
 * The initial screen shown to the user. It now serves as a simple entry point to the main app.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the button from the layout and set a click listener to open the main activity.
        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish this activity to prevent the user from coming back to it.
        });
    }
}
