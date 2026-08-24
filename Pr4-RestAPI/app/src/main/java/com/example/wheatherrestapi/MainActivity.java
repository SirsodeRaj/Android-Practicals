package com.example.weatherrestapi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private EditText etCity;
    private Button btnGetWeather;

    private TextView tvCity;
    private TextView tvTemperature;
    private TextView tvWeather;
    private TextView tvFeelsLike;
    private TextView tvHumidity;
    private TextView tvError;

    private ProgressBar progressBar;

    // We will add the working API key later
    private static final String API_KEY = "a92bab35bf12eb60579827b8f73f9a1d";

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect XML components with Java
        etCity = findViewById(R.id.etCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);

        tvCity = findViewById(R.id.tvCity);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeather = findViewById(R.id.tvWeather);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvError = findViewById(R.id.tvError);

        progressBar = findViewById(R.id.progressBar);

        // Button click event
        btnGetWeather.setOnClickListener(v -> {

            String city = etCity.getText().toString().trim();

            if (city.isEmpty()) {
                etCity.setError("Please enter city name");
                return;
            }

            getWeather(city);
        });
    }

    private void getWeather(String city) {

        progressBar.setVisibility(View.VISIBLE);
        tvError.setText("");
        btnGetWeather.setEnabled(false);

        executorService.execute(() -> {

            try {

                String encodedCity =
                        URLEncoder.encode(city, "UTF-8");

                String apiUrl =
                        "https://api.openweathermap.org/data/2.5/weather"
                                + "?q=" + encodedCity
                                + "&appid=" + API_KEY
                                + "&units=metric";

                URL url = new URL(apiUrl);

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode =
                        connection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {
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
                connection.disconnect();

                if (responseCode == 200) {

                    JSONObject jsonResponse =
                            new JSONObject(response.toString());

                    // Get weather information
                    JSONArray weatherArray =
                            jsonResponse.getJSONArray("weather");

                    JSONObject weatherObject =
                            weatherArray.getJSONObject(0);

                    String weatherCondition =
                            weatherObject.getString("description");

                    // Get temperature and humidity
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

                    // Update UI
                    runOnUiThread(() -> {

                        tvCity.setText(cityName);

                        tvTemperature.setText(
                                String.format("%.1f °C", temperature)
                        );

                        tvWeather.setText(
                                capitalize(weatherCondition)
                        );

                        tvFeelsLike.setText(
                                String.format(
                                        "Feels like: %.1f °C",
                                        feelsLike
                                )
                        );

                        tvHumidity.setText(
                                "Humidity: " + humidity + " %"
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
                            "Network error: " + e.getMessage()
                    );
                });
            }
        });
    }

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