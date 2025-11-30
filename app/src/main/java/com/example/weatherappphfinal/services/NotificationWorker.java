package com.example.weatherappphfinal.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.weatherappphfinal.R;
import com.example.weatherappphfinal.ui.MainActivity;
import java.util.Locale;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "weather_notification_channel";
    public static final String KEY_LOCATION_NAME = "location_name";
    public static final String KEY_TEMP = "temp";
    public static final String KEY_DESC = "desc";
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_WIND_SPEED = "wind_speed";
    public static final String KEY_PRECIPITATION = "precipitation";
    public static final String KEY_PRESSURE = "pressure";


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String locationName = getInputData().getString(KEY_LOCATION_NAME);
        double temp = getInputData().getDouble(KEY_TEMP, 0);
        String desc = getInputData().getString(KEY_DESC);
        int humidity = getInputData().getInt(KEY_HUMIDITY, 0);
        double windSpeed = getInputData().getDouble(KEY_WIND_SPEED, 0);
        int precipitation = getInputData().getInt(KEY_PRECIPITATION, 0);
        double pressure = getInputData().getDouble(KEY_PRESSURE, 0);

        sendNotification(locationName, temp, desc, humidity, windSpeed, precipitation, pressure);
        return Result.success();
    }

    private void sendNotification(String locationName, double temperature, String weatherDescription, int humidity, double windSpeed, int precipitationProbability, double pressure) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Weather Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String title = String.format(Locale.US, "Weather for %s: %d°C, %s",
                locationName,
                (int) temperature,
                weatherDescription);

        String bigText = String.format(Locale.US,
                "Humidity: %d%%\n" +
                        "Wind Speed: %.1f m/s\n" +
                        "Precipitation: %d%%\n" +
                        "Pressure: %.0f hPa",
                humidity,
                windSpeed,
                precipitationProbability,
                pressure);

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
