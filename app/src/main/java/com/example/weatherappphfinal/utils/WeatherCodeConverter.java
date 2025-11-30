package com.example.weatherappphfinal.utils;

public class WeatherCodeConverter {

    public static WeatherCondition convert(int code) {
        switch (code) {
            case 0: return new WeatherCondition("Clear sky", "Clear and sunny", "☀️");
            case 1:
            case 2:
            case 3: return new WeatherCondition("Partly cloudy", "Some clouds", "⛅");
            case 45:
            case 48: return new WeatherCondition("Foggy", "Fog or mist", "🌫️");
            case 51:
            case 53:
            case 55: return new WeatherCondition("Drizzle", "Light rain", "🌦️");
            case 61:
            case 63:
            case 65: return new WeatherCondition("Rain", "Rainy weather", "🌧️");
            case 66:
            case 67: return new WeatherCondition("Freezing rain", "Cold rain", "🌧️");
            case 71:
            case 73:
            case 75: return new WeatherCondition("Snow", "Snowing", "❄️");
            case 77: return new WeatherCondition("Snow grains", "Light snow", "❄️");
            case 80:
            case 81:
            case 82: return new WeatherCondition("Rain showers", "Heavy rain", "⛈️");
            case 85:
            case 86: return new WeatherCondition("Snow showers", "Snow showers", "🌨️");
            case 95: return new WeatherCondition("Thunderstorm", "Thunder and lightning", "⛈️");
            case 96:
            case 99: return new WeatherCondition("Thunderstorm with hail", "Severe storm", "⛈️");
            default: return new WeatherCondition("Unknown", "Weather unknown", "🌤️");
        }
    }

    public static class WeatherCondition {
        public String condition;
        public String description;
        public String emoji;

        public WeatherCondition(String condition, String description, String emoji) {
            this.condition = condition;
            this.description = description;
            this.emoji = emoji;
        }
    }
}
