package com.example.weatherappphfinal.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherappphfinal.models.WeatherModel;

/**
 * A ViewModel designed to store and manage UI-related data in a lifecycle-conscious way.
 * This class allows data to survive configuration changes such as screen rotations.
 */
public class MainViewModel extends ViewModel {
    // LiveData holding the current weather information. It is private and mutable so that only
    // the ViewModel can change its value.
    private final MutableLiveData<WeatherModel> weather = new MutableLiveData<>();

    // LiveData holding the name of the current city.
    private final MutableLiveData<String> cityName = new MutableLiveData<>();

    /**
     * Updates the weather LiveData with a new WeatherModel object.
     * This will trigger any active observers.
     *
     * @param weather The new weather data to set.
     */
    public void setWeather(WeatherModel weather) {
        this.weather.setValue(weather);
    }

    /**
     * Returns the LiveData object for the weather. UI components can observe this to get updates.
     * This is the public, non-mutable version to prevent external classes from changing the value.
     *
     * @return A LiveData object containing the WeatherModel.
     */
    public LiveData<WeatherModel> getWeather() {
        return weather;
    }

    /**
     * Updates the cityName LiveData with a new city name.
     *
     * @param cityName The new city name to set.
     */
    public void setCityName(String cityName) {
        this.cityName.setValue(cityName);
    }

    /**
     * Returns the LiveData object for the city name. UI components can observe this.
     *
     * @return A LiveData object containing the city name string.
     */
    public LiveData<String> getCityName() {
        return cityName;
    }
}
