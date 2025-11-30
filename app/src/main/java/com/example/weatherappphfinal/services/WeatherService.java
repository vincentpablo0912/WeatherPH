package com.example.weatherappphfinal.services;

import com.example.weatherappphfinal.models.DailyForecastModel;
import com.example.weatherappphfinal.models.WeatherModel;
import com.example.weatherappphfinal.utils.WeatherCodeConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A service class responsible for fetching weather data from the Open-Meteo API.
 * This class handles the network request and parses the JSON response into a WeatherModel.
 */
public class WeatherService {

    /**
     * Fetches weather data for a given latitude and longitude.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A WeatherModel object containing the weather data, or null if an error occurs.
     */
    public static WeatherModel getWeather(double latitude, double longitude) {
        try {
            // Construct the API URL with the required parameters.
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                    "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,precipitation,surface_pressure,cloud_cover" +
                    "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_mean" +
                    "&timezone=Asia/Singapore";

            // Make the network request and get the JSON response.
            JSONObject response = ApiClient.get(url);
            if (response == null) return null;

            // Extract the main JSON objects from the response.
            JSONObject current = response.getJSONObject("current");
            JSONObject daily = response.getJSONObject("daily");

            // Parse the daily forecast data.
            List<DailyForecastModel> dailyForecasts = new ArrayList<>();
            JSONArray dailyTime = daily.getJSONArray("time");
            JSONArray dailyWeatherCodes = daily.getJSONArray("weather_code");
            JSONArray maxTemps = daily.getJSONArray("temperature_2m_max");
            JSONArray minTemps = daily.getJSONArray("temperature_2m_min");
            JSONArray precipProb = daily.getJSONArray("precipitation_probability_mean");

            for (int i = 0; i < dailyTime.length(); i++) {
                dailyForecasts.add(new DailyForecastModel(
                        dailyTime.getString(i),
                        dailyWeatherCodes.getInt(i),
                        (int) Math.round(maxTemps.getDouble(i)),
                        (int) Math.round(minTemps.getDouble(i)),
                        precipProb.getInt(i)
                ));
            }

            // Convert the current weather code to a human-readable condition.
            WeatherCodeConverter.WeatherCondition currentCondition = WeatherCodeConverter.convert(current.getInt("weather_code"));

            // Construct and return the WeatherModel with the parsed data.
            return new WeatherModel(
                    current.getDouble("temperature_2m"),
                    current.getInt("relative_humidity_2m"),
                    current.getDouble("wind_speed_10m"),
                    current.getInt("weather_code"),
                    currentCondition.description,
                    dailyForecasts.get(0).getPrecipitationProbability(),
                    current.getDouble("precipitation"),
                    current.getDouble("surface_pressure"),
                    current.getInt("cloud_cover"),
                    dailyForecasts
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
