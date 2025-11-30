package com.example.weatherappphfinal.managers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import com.example.weatherappphfinal.listeners.WeatherListener;
import com.example.weatherappphfinal.models.LocationModel;
import com.example.weatherappphfinal.models.WeatherModel;
import com.example.weatherappphfinal.services.LocationService;
import com.example.weatherappphfinal.services.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherManager {

    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final FusedLocationProviderClient fusedLocationClient;
    private final Activity activity;

    public WeatherManager(Activity activity) {
        this.activity = activity;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void loadWeather(String locationName, WeatherListener listener) {
        executorService.execute(() -> {
            LocationModel location = LocationService.getPhilippineLocation(locationName);
            if (location == null) {
                mainHandler.post(() -> listener.onWeatherError("Location not found"));
                return;
            }

            WeatherModel weather = WeatherService.getWeather(location.getLatitude(), location.getLongitude());
            if (weather == null) {
                mainHandler.post(() -> listener.onWeatherError("Failed to fetch weather"));
                return;
            }

            mainHandler.post(() -> listener.onWeatherLoaded(weather, location.getName()));
        });
    }

    public void loadWeather(double latitude, double longitude, WeatherListener listener) {
        executorService.execute(() -> {
            WeatherModel weather = WeatherService.getWeather(latitude, longitude);
            if (weather == null) {
                mainHandler.post(() -> listener.onWeatherError("Failed to fetch weather for your location"));
                return;
            }

            String locationName = LocationService.getLocationNameFromCoordinates(latitude, longitude);
            if (locationName == null) {
                locationName = "Your Location";
            }

            final String finalLocationName = locationName;
            mainHandler.post(() -> listener.onWeatherLoaded(weather, finalLocationName));
        });
    }

    public void fetchCurrentLocation(WeatherListener listener) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mainHandler.post(() -> listener.onWeatherError("Location permission not granted"));
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        loadWeather(location.getLatitude(), location.getLongitude(), listener);
                    } else {
                        mainHandler.post(() -> listener.onWeatherError("Could not get current location"));
                    }
                })
                .addOnFailureListener(activity, e -> mainHandler.post(() -> listener.onWeatherError("Failed to get current location")));
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
