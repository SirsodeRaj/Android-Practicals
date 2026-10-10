package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView etCity;
    private Button btnGetWeather;

    private TextView tvCity;
    private TextView tvTemperature;
    private TextView tvWeather;
    private TextView tvFeelsLike;
    private TextView tvHumidity;
    private TextView tvError;
    private TextView tvWeatherIcon;

    private ProgressBar progressBar;

    // We will add the working API key later
    private static final String API_KEY = "93108075c2b5a3709e498be25fb876a1";

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        etCity = findViewById(R.id.etCity);

        String[] cities = {
                "London",
                "Pune",
                "Mumbai",
                "Delhi",
                "Nagpur",
                "Nashik",
                "Bengaluru",
                "Hyderabad",
                "Chennai",
                "Kolkata",
                "Jaipur",
                "Ahmedabad",
                "New York",
                "Paris",
                "Dubai"
        };

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                this,
                R.layout.city_dropdown_item,
                R.id.citySuggestion,
                cities
        );

        etCity.setAdapter(cityAdapter);
        etCity.setThreshold(1);

        // Connect XML views with Java

        btnGetWeather = findViewById(R.id.btnGetWeather);

        tvCity = findViewById(R.id.tvCity);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeather = findViewById(R.id.tvWeather);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvError = findViewById(R.id.tvError);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);

        progressBar = findViewById(R.id.progressBar);

        // Get Weather button
        btnGetWeather.setOnClickListener(v -> {

            String city = etCity.getText().toString().trim();

            if (city.isEmpty()) {
                etCity.setError("Please enter a city name");
                return;
            }

            getWeather(city);
        });
    }

    private void getWeather(String city) {

        progressBar.setVisibility(View.VISIBLE);
        btnGetWeather.setEnabled(false);
        tvError.setText("");

        executorService.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String encodedCity =
                        URLEncoder.encode(city, "UTF-8");

                String apiUrl =
                        "https://api.openweathermap.org/data/2.5/weather"
                                + "?q=" + encodedCity
                                + "&appid=" + API_KEY
                                + "&units=metric";

                URL url = new URL(apiUrl);

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode =
                        connection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200 &&
                        responseCode < 300) {

                    inputStream = connection.getInputStream();

                } else {

                    inputStream = connection.getErrorStream();
                }

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(inputStream)
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                if (responseCode == 200) {

                    JSONObject jsonResponse =
                            new JSONObject(response.toString());

                    // Weather information
                    JSONArray weatherArray =
                            jsonResponse.getJSONArray("weather");

                    JSONObject weatherObject =
                            weatherArray.getJSONObject(0);

                    String weatherCondition =
                            weatherObject.getString("description");

                    String weatherMain =
                            weatherObject.getString("main");

                    // Main weather data
                    JSONObject mainObject =
                            jsonResponse.getJSONObject("main");

                    double temperature =
                            mainObject.getDouble("temp");

                    double feelsLike =
                            mainObject.getDouble("feels_like");

                    int humidity =
                            mainObject.getInt("humidity");

                    String cityName =
                            jsonResponse.getString("name");

                    runOnUiThread(() -> {

                        tvCity.setText(cityName);

                        tvTemperature.setText(
                                String.format(
                                        "%.1f °C",
                                        temperature
                                )
                        );

                        tvWeather.setText(
                                capitalize(weatherCondition)
                        );

                        tvFeelsLike.setText(
                                String.format(
                                        "%.1f °C",
                                        feelsLike
                                )
                        );

                        tvHumidity.setText(
                                humidity + " %"
                        );

                        // Change weather icon
                        tvWeatherIcon.setText(
                                getWeatherIcon(weatherMain)
                        );

                        progressBar.setVisibility(View.GONE);
                        btnGetWeather.setEnabled(true);
                    });

                } else {

                    JSONObject errorObject =
                            new JSONObject(response.toString());

                    String errorMessage =
                            errorObject.optString(
                                    "message",
                                    "Unable to get weather data"
                            );

                    runOnUiThread(() -> {

                        progressBar.setVisibility(View.GONE);
                        btnGetWeather.setEnabled(true);

                        tvError.setText(
                                "Error: " + errorMessage
                        );
                    });
                }

            } catch (Exception e) {

                runOnUiThread(() -> {

                    progressBar.setVisibility(View.GONE);
                    btnGetWeather.setEnabled(true);

                    tvError.setText(
                            "Network error. Please check your connection."
                    );
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // Convert weather condition into an icon
    private String getWeatherIcon(String weather) {

        switch (weather.toLowerCase()) {

            case "clear":
                return "☀️";

            case "clouds":
                return "☁️";

            case "rain":
                return "🌧️";

            case "drizzle":
                return "🌦️";

            case "thunderstorm":
                return "⛈️";

            case "snow":
                return "❄️";

            case "mist":
            case "fog":
            case "haze":
                return "🌫️";

            default:
                return "🌤️";
        }
    }

    // Capitalize first letter
    private String capitalize(String text) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.substring(0, 1).toUpperCase()
                + text.substring(1);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        executorService.shutdown();
    }
}