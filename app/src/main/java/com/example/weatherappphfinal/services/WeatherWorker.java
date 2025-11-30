package com.example.weatherappphfinal.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weatherappphfinal.R;
import com.example.weatherappphfinal.models.LocationModel;
import com.example.weatherappphfinal.models.WeatherModel;
import com.example.weatherappphfinal.ui.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class WeatherWorker extends Worker {

    private static final String CHANNEL_ID = "weather_notification_channel";

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGpsEnabled && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return fetchCurrentLocationWeather();
        } else {
            return fetchLastSearchedCityWeather();
        }
    }

    private Result fetchCurrentLocationWeather() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        try {
            Task<Location> locationTask = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            Location location = Tasks.await(locationTask);
            if (location != null) {
                WeatherModel weather = WeatherService.getWeather(location.getLatitude(), location.getLongitude());
                String locationName = LocationService.getLocationNameFromCoordinates(location.getLatitude(), location.getLongitude());
                if (weather != null && locationName != null) {
                    sendNotification(locationName, weather);
                    return Result.success();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }

    private Result fetchLastSearchedCityWeather() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE);
        String lastCity = prefs.getString("lastCity", "Manila");

        LocationModel location = LocationService.getPhilippineLocation(lastCity);
        if (location == null) {
            return Result.failure();
        }

        WeatherModel weather = WeatherService.getWeather(location.getLatitude(), location.getLongitude());
        if (weather != null) {
            sendNotification(location.getName(), weather);
            return Result.success();
        }
        return Result.failure();
    }

    private void sendNotification(String locationName, WeatherModel weather) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Weather Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String title = String.format(Locale.US, "Weather for %s: %d°C, %s",
                locationName,
                (int) weather.getTemperature(),
                weather.getWeatherDescription());

        String bigText = String.format(Locale.US,
                "Humidity: %d%%\n" +
                        "Wind Speed: %.1f m/s\n" +
                        "Precipitation: %d%%\n" +
                        "Pressure: %.0f hPa",
                weather.getHumidity(),
                weather.getWindSpeed(),
                weather.getPrecipitationProbability(),
                weather.getPressure());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_cloud)
                .setContentTitle("Daily Weather Forecast")
                .setContentText(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
