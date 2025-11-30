package com.example.weatherappphfinal.services;

import com.example.weatherappphfinal.models.LocationModel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A service class for handling location-related operations,
 * such as geocoding and providing a list of predefined locations.
 */
public class LocationService {

    // A predefined list of major locations in the Philippines for search suggestions.
    private static final String[] PHILIPPINE_LOCATIONS = {
            "Bacolod", "Baguio", "Cagayan de Oro", "Cebu City", "Davao City", "General Santos",
            "Iligan", "Iloilo City", "Lapu-Lapu", "Las Piñas", "Makati", "Malabon", "Mandaluyong",
            "Mandaue", "Manila", "Marikina", "Muntinlupa", "Navotas", "Olongapo", "Parañaque",
            "Pasay", "Pasig", "Puerto Princesa", "Quezon City", "San Juan", "Tacloban", "Taguig",
            "Valenzuela", "Zamboanga City"
    };

    /**
     * Returns a predefined list of all major Philippine locations.
     * @return A string array of location names.
     */
    public static String[] getAllPhilippineLocations() {
        return PHILIPPINE_LOCATIONS;
    }

    /**
     * Fetches the geographical details (latitude, longitude) for a given location name.
     * It uses the Open-Meteo Geocoding API and specifically filters for results within the Philippines.
     *
     * @param locationName The name of the location to search for.
     * @return A LocationModel object if a Philippine location is found, otherwise null.
     */
    public static LocationModel getPhilippineLocation(String locationName) {
        try {
            // Format the location name for the URL (e.g., replace spaces with '+').
            locationName = locationName.replaceAll(" ", "+");
            String url = "https://geocoding-api.open-meteo.com/v1/search?" +
                    "name=" + locationName + "&count=10&language=en&format=json";

            // Make the API call.
            JSONObject response = ApiClient.get(url);
            if (response == null) return null;

            // Parse the JSON response to find a matching location.
            JSONArray results = response.getJSONArray("results");
            if (results.length() == 0) return null;

            // Iterate through the results and return the first match that is in the Philippines (PH).
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                if (item.getString("country_code").equals("PH")) {
                    return new LocationModel(
                            item.getString("name"),
                            item.getDouble("latitude"),
                            item.getDouble("longitude"),
                            item.getString("country_code")
                    );
                }
            }

            return null; // Return null if no location in the Philippines is found.

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Performs a reverse geocoding lookup to get a location name from coordinates.
     * It uses the BigDataCloud API.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return The locality or city name if found, otherwise null.
     */
    public static String getLocationNameFromCoordinates(double latitude, double longitude) {
        try {
            String url = "https://api.bigdatacloud.net/data/reverse-geocode-client?" +
                    "latitude=" + latitude + "&longitude=" + longitude + "&localityLanguage=en";

            JSONObject response = ApiClient.get(url);
            if (response != null) {
                // The API may return the location name under "locality" or "city".
                if (response.has("locality")) {
                    return response.getString("locality");
                } else if (response.has("city")) {
                    return response.getString("city");
                }
            }
            return null; // Return null if no name can be determined.

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
