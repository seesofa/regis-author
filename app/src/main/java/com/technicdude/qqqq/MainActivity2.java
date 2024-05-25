package com.technicdude.qqqq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class MainActivity2 extends AppCompatActivity {
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;
    private ImageView imageView3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);
        imageView3 = findViewById(R.id.imageView3);
        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Если ничего не ввели в поле, то выдаем всплывающую подсказку
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity2.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {
                    // Если ввели, то формируем ссылку для получения погоды
                    String city = user_field.getText().toString();
                    String key = "8cf8d8ac4ce5c3a63d7418d7a01cd973";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    // Запускаем класс для получения погоды
                    new GetURLData().execute(url);
                }
            }
        });
        checkLocationPermisson();
    }
public void checkLocationPermisson(){
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    } else {
        requestLocationUpdates();
    }
}
    public void requestLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null) {
            @SuppressLint("MissingPermission")
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String key = "8cf8d8ac4ce5c3a63d7418d7a01cd973";
                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + key + "&units=metric&lang=ru";

                // Запускаем класс для получения погоды
                new GetURLData().execute(url);
            } else {
                // Местоположение не доступно
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                // Handle permission denied
            }
        }
    }
    private class GetURLData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView3.setImageResource(android.R.color.transparent);
            result_info.setText("Ожидайте...");
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                // Выведите сообщение об ошибке
                result_info.setText("Ошибка получения данных о погоде");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject mainObject = jsonObject.getJSONObject("main");
                int temperature = mainObject.getInt("temp");
                int humidity = mainObject.getInt("humidity");
                double seaLevel = mainObject.optDouble("sea_level", 0.0);

                String iconBaseUrl = "https://openweathermap.org/img/w/";
                String iconExtension = ".png";
                String iconId = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconUrl = iconBaseUrl + iconId + iconExtension;

                Glide.with(MainActivity2.this)
                        .load(iconUrl)
                        .into(imageView3);

                runOnUiThread(() -> {
                    result_info.setText("Температура: " + temperature + " °C\n" +
                            "Влажность: " + humidity + "%\n" +
                            "Уровень моря: " + seaLevel + " м");
                });

            } catch (JSONException e) {
                e.printStackTrace();
                // Выведите сообщение об ошибке
                runOnUiThread(() -> {
                    result_info.setText("Ошибка обработки данных о погоде");
                });
            }
        }
    }
}
