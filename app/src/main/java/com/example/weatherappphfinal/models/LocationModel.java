package com.example.weatherappphfinal.models;

/**
 * Represents a geographical location with its name and coordinates.
 * This model is used to store location data fetched from geocoding services.
 */
public class LocationModel {
    // The name of the location (e.g., city name).
    private String name;
    // The latitude of the location.
    private double latitude;
    // The longitude of the location.
    private double longitude;
    // The country code of the location (e.g., "PH" for Philippines).
    private String countryCode;

    /**
     * Constructs a new LocationModel.
     *
     * @param name        The name of the location.
     * @param latitude    The latitude of the location.
     * @param longitude   The longitude of the location.
     * @param countryCode The country code of the location.
     */
    public LocationModel(String name, double latitude, double longitude, String countryCode) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
    }

    // Getters for all the location data fields.

    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCountryCode() { return countryCode; }
}
