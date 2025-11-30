package com.example.weatherappphfinal.listeners;

import com.example.weatherappphfinal.models.WeatherModel;

public interface WeatherListener {
    void onWeatherLoaded(WeatherModel weather, String cityName);
    void onWeatherError(String message);
}