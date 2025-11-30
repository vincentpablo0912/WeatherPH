package com.example.weatherappphfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherappphfinal.R;
import com.example.weatherappphfinal.ui.MainActivity;

/**
 * OnboardingActivity is responsible for greeting the user and capturing their name.
 * This is typically shown only on the first launch of the app.
 */
public class OnboardingActivity extends AppCompatActivity {

    // UI elements for name input and submission.
    private EditText nameEditText;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Initialize views from the layout.
        nameEditText = findViewById(R.id.nameEditText);
        continueButton = findViewById(R.id.continueButton);

        // Set a listener for the continue button.
        continueButton.setOnClickListener(v -> {
            // Get the name from the EditText and remove any leading/trailing whitespace.
            String name = nameEditText.getText().toString().trim();

            // Validate that the name is not empty.
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else {
                // If the name is valid, save it to SharedPreferences.
                SharedPreferences prefs = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", name);
                editor.apply(); // Apply the changes asynchronously.

                // Navigate to the main screen.
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish this activity so the user cannot navigate back to it.
            }
        });
    }
}
