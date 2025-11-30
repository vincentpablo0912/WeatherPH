package com.example.weatherappphfinal.models;

/**
 * Represents the weather forecast for a single day.
 * This is a simple data model class to hold daily forecast information.
 */
public class DailyForecastModel {

    // The date of the forecast (e.g., "2023-12-25").
    private String date;
    // A numerical code representing the weather condition (e.g., sunny, cloudy).
    private int weatherCode;
    // The maximum expected temperature for the day.
    private int maxTemp;
    // The minimum expected temperature for the day.
    private int minTemp;
    // The probability of precipitation as a percentage.
    private int precipitationProbability;

    /**
     * Constructs a new DailyForecastModel.
     *
     * @param date                   The date of the forecast.
     * @param weatherCode            The weather condition code.
     * @param maxTemp                The maximum temperature.
     * @param minTemp                The minimum temperature.
     * @param precipitationProbability The probability of precipitation.
     */
    public DailyForecastModel(String date, int weatherCode, int maxTemp, int minTemp, int precipitationProbability) {
        this.date = date;
        this.weatherCode = weatherCode;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.precipitationProbability = precipitationProbability;
    }

    // Getters for all the forecast data fields.

    public String getDate() { return date; }
    public int getWeatherCode() { return weatherCode; }
    public int getMaxTemp() { return maxTemp; }
    public int getMinTemp() { return minTemp; }
    public int getPrecipitationProbability() { return precipitationProbability; }
}
