package com.example.weatherappphfinal.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.weatherappphfinal.R;
import com.example.weatherappphfinal.listeners.WeatherListener;
import com.example.weatherappphfinal.managers.WeatherManager;
import com.example.weatherappphfinal.models.DailyForecastModel;
import com.example.weatherappphfinal.models.WeatherModel;
import com.example.weatherappphfinal.services.LocationService;
import com.example.weatherappphfinal.services.WeatherWorker;
import com.example.weatherappphfinal.utils.WeatherCodeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements WeatherListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private AutoCompleteTextView searchEditText;
    private Button searchButton;
    private ImageButton exitButton, refreshButton;
    private TextView greetingText, locationText, dateText, weatherIcon, temperatureText;
    private TextView feelsLikeText, weatherDescText, humidityValue, windSpeedValue, precipitationValue, pressureValue;
    private ProgressBar loadingProgress;
    private LinearLayout forecastContainer;

    private MainViewModel mainViewModel;
    private WeatherManager weatherManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        weatherManager = new WeatherManager(this);

        initViews();
        setupUI();

        if (mainViewModel.getWeather().getValue() != null && mainViewModel.getCityName().getValue() != null) {
            updateUI(mainViewModel.getWeather().getValue(), mainViewModel.getCityName().getValue());
        } else {
            handleInitialLoad();
        }

        scheduleDailyWeatherNotification();
        setupClickListeners();
    }

    private void initViews() {
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        exitButton = findViewById(R.id.exitButton);
        refreshButton = findViewById(R.id.refreshButton);
        greetingText = findViewById(R.id.greetingText);
        locationText = findViewById(R.id.locationText);
        dateText = findViewById(R.id.dateText);
        weatherIcon = findViewById(R.id.weatherIcon);
        temperatureText = findViewById(R.id.temperatureText);
        feelsLikeText = findViewById(R.id.feelsLikeText);
        weatherDescText = findViewById(R.id.weatherDescText);
        humidityValue = findViewById(R.id.humidityValue);
        windSpeedValue = findViewById(R.id.windSpeedValue);
        precipitationValue = findViewById(R.id.precipitationValue);
        pressureValue = findViewById(R.id.pressureValue);
        loadingProgress = findViewById(R.id.loadingProgress);
        forecastContainer = findViewById(R.id.forecastContainer);
    }

    private void setupUI() {
        sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");
        greetingText.setText(userName.isEmpty() ? "Here's the weather today" : "Here's the weather today, " + userName);

        String[] locations = LocationService.getAllPhilippineLocations();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        searchEditText.setAdapter(adapter);
    }

    private void handleInitialLoad() {
        String lastCity = sharedPreferences.getString("lastCity", "Manila");
        loadWeather(lastCity);
    }

    private void setupClickListeners() {
        exitButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.exit_confirmation)
                    .setMessage(R.string.exit_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                    .setNegativeButton(R.string.no, null)
                    .show();
        });

        searchButton.setOnClickListener(v -> {
            String location = searchEditText.getText().toString().trim();
            if (!location.isEmpty()) {
                loadWeather(location);
            } else {
                Toast.makeText(this, "Enter location", Toast.LENGTH_SHORT).show();
            }
        });

        refreshButton.setOnClickListener(v -> {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else {
                requestLocationAndLoadWeather();
            }
        });
    }

    private void requestLocationAndLoadWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            loadingProgress.setVisibility(View.VISIBLE);
            setButtonsEnabled(false);
            weatherManager.fetchCurrentLocation(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationAndLoadWeather();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleDailyWeatherNotification() {
        PeriodicWorkRequest weatherWorkRequest = new PeriodicWorkRequest.Builder(WeatherWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(this).enqueue(weatherWorkRequest);
    }

    private long calculateInitialDelay() {
        Calendar now = Calendar.getInstance();
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, 7);
        nextRun.set(Calendar.MINUTE, 0);
        nextRun.set(Calendar.SECOND, 0);
        if (now.after(nextRun)) {
            nextRun.add(Calendar.DAY_OF_YEAR, 1);
        }
        return nextRun.getTimeInMillis() - now.getTimeInMillis();
    }

    private void loadWeather(String locationName) {
        loadingProgress.setVisibility(View.VISIBLE);
        setButtonsEnabled(false);
        weatherManager.loadWeather(locationName, this);
    }

    @Override
    public void onWeatherLoaded(WeatherModel weather, String cityName) {
        updateUI(weather, cityName);
    }

    @Override
    public void onWeatherError(String message) {
        loadingProgress.setVisibility(View.GONE);
        setButtonsEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void updateUI(WeatherModel weather, String cityName) {
        mainViewModel.setWeather(weather);
        mainViewModel.setCityName(cityName);

        loadingProgress.setVisibility(View.GONE);
        setButtonsEnabled(true);

        locationText.setText(cityName);
        searchEditText.setText("");

        temperatureText.setText(String.format("%d°C", (int) weather.getTemperature()));
        WeatherCodeConverter.WeatherCondition currentCondition = WeatherCodeConverter.convert(weather.getWeatherCode());
        feelsLikeText.setText(currentCondition.description);
        weatherDescText.setText(currentCondition.condition);
        humidityValue.setText(String.format("%d%%", weather.getHumidity()));
        windSpeedValue.setText(String.format("%.1f m/s", weather.getWindSpeed()));
        precipitationValue.setText(String.format("%d%%", weather.getPrecipitationProbability()));
        pressureValue.setText(String.format("%.0f hPa", weather.getPressure()));
        weatherIcon.setText(currentCondition.emoji);
        dateText.setText(new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(new Date()));

        updateForecast(weather);

        sharedPreferences.edit().putString("lastCity", cityName).apply();
    }

    private void setButtonsEnabled(boolean enabled) {
        searchButton.setEnabled(enabled);
        exitButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
    }

    private void updateForecast(WeatherModel weather) {
        forecastContainer.removeAllViews();
        for (DailyForecastModel daily : weather.getDailyForecast()) {
            WeatherCodeConverter.WeatherCondition cond = WeatherCodeConverter.convert(daily.getWeatherCode());
            forecastContainer.addView(createForecastItem(daily.getDate(), cond.emoji, daily.getMaxTemp(), daily.getMinTemp(), cond.condition));
        }
    }

    private View createForecastItem(String date, String emoji, int maxTemp, int minTemp, String condition) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(16, 24, 16, 24);
        item.setBackgroundResource(R.drawable.forecast_bubble);
        item.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (110 * getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        item.setLayoutParams(params);

        TextView dayText = new TextView(this);
        dayText.setText(formatDateToDay(date));
        dayText.setGravity(Gravity.CENTER);
        dayText.setTextColor(Color.WHITE);

        TextView dateText = new TextView(this);
        dateText.setText(formatDateToMonthDay(date));
        dateText.setGravity(Gravity.CENTER);
        dateText.setTextColor(Color.WHITE);

        TextView iconText = new TextView(this);
        iconText.setText(emoji);
        iconText.setTextSize(40);
        iconText.setGravity(Gravity.CENTER);

        TextView condText = new TextView(this);
        condText.setText(condition);
        condText.setGravity(Gravity.CENTER);
        condText.setSingleLine(true);
        condText.setTextColor(Color.WHITE);

        TextView tempText = new TextView(this);
        tempText.setText(String.format("%d°/%d°", maxTemp, minTemp));
        tempText.setGravity(Gravity.CENTER);
        tempText.setTextColor(Color.WHITE);

        item.addView(dayText);
        item.addView(dateText);
        item.addView(iconText);
        item.addView(condText);
        item.addView(tempText);

        return item;
    }

    private String formatDateToDay(String dateString) {
        try {
            return new SimpleDateFormat("EEE", Locale.getDefault()).format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    private String formatDateToMonthDay(String dateString) {
        try {
            return new SimpleDateFormat("MM/dd", Locale.getDefault()).format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherManager.shutdown();
    }
}
