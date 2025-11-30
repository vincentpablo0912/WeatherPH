package com.example.weatherappphfinal.models;

import java.util.List;

/**
 * Represents the complete weather data for a specific location.
 * This model holds the current weather conditions, as well as the daily forecast.
 */
public class WeatherModel {

    // Current temperature in Celsius.
    private double temperature;
    // Current humidity percentage.
    private int humidity;
    // Current wind speed in meters per second.
    private double windSpeed;
    // Weather code representing the current conditions.
    private int weatherCode;
    // A human-readable description of the current weather.
    private String weatherDescription;
    // The probability of precipitation as a percentage.
    private int precipitationProbability;
    // The amount of precipitation.
    private double precipitation;
    // The atmospheric pressure in hPa.
    private double pressure;
    // The percentage of the sky covered by clouds.
    private int cloudCover;
    // A list of daily forecast data.
    private List<DailyForecastModel> dailyForecast;

    /**
     * Constructs a new WeatherModel with all the necessary weather data.
     *
     * @param temperature            Current temperature.
     * @param humidity               Current humidity.
     * @param windSpeed              Current wind speed.
     * @param weatherCode            Current weather code.
     * @param weatherDescription     Description of the current weather.
     * @param precipitationProbability Probability of precipitation.
     * @param precipitation          Amount of precipitation.
     * @param pressure               Atmospheric pressure.
     * @param cloudCover             Cloud cover percentage.
     * @param dailyForecast          List of daily forecasts.
     */
    public WeatherModel(double temperature, int humidity, double windSpeed, int weatherCode, String weatherDescription, int precipitationProbability, double precipitation, double pressure, int cloudCover, List<DailyForecastModel> dailyForecast) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherCode = weatherCode;
        this.weatherDescription = weatherDescription;
        this.precipitationProbability = precipitationProbability;
        this.precipitation = precipitation;
        this.pressure = pressure;
        this.cloudCover = cloudCover;
        this.dailyForecast = dailyForecast;
    }

    // Getters for all the weather data fields.

    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public int getWeatherCode() { return weatherCode; }
    public String getWeatherDescription() { return weatherDescription; }
    public int getPrecipitationProbability() { return precipitationProbability; }
    public double getPrecipitation() { return precipitation; }
    public double getPressure() { return pressure; }
    public int getCloudCover() { return cloudCover; }
    public List<DailyForecastModel> getDailyForecast() { return dailyForecast; }
}
