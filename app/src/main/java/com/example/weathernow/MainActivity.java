package com.example.weathernow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPb;
    private TextView tvCityName,tvTemperature,tvCondition;
    private TextInputEditText edtCity;
    private ImageView ivBack,ivSearch,ivIcon;
    private RecyclerView rvWeather;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList;
    private WeatherRvAdapter weatherRvAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.rlHome);
        loadingPb = findViewById(R.id.pbLoading);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        edtCity = findViewById(R.id.edtCity);
        ivSearch = findViewById(R.id.ivSearch);
        ivIcon = findViewById(R.id.ivIcon);
        ivBack = findViewById(R.id.ivBack);
        rvWeather = findViewById(R.id.rvWeather);

        weatherRvModelArrayList = new ArrayList<>();
        weatherRvAdapter = new WeatherRvAdapter(this,weatherRvModelArrayList);
        rvWeather.setAdapter(weatherRvAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

       Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            cityName = getCityName(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
        }else {
            cityName = "London";
            getWeatherInfo(cityName);
        }


        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtCity.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else {
                    tvCityName.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude , double latitude){
        String cityName = "Not found";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,10);
            for (Address address : addresses){
                if (address != null){
                    String city = address.getLocality();
                    if (city!= null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {

        String url = "http://api.weatherapi.com/v1/forecast.json?key=e81760bfa04f4107b8b125711222803&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        tvCityName.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPb.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRvModelArrayList.clear();
                try {
                    String temperature =response.getJSONObject("current").getString("temp_c");
                    tvTemperature.setText(temperature +" °C");
                    int isDay  = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    String code = response.getJSONObject("current").getJSONObject("condition").getString("code");
                    Picasso.get().load("http:".concat(icon)).into(ivIcon);
                    tvCondition.setText(condition);
                    if (isDay == 1){
                        Picasso.get().load("https://images.unsplash.com/photo-1566228015668-4c45dbc4e2f5?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(ivBack);
                    }else {
                        Picasso.get().load("https://images.unsplash.com/photo-1532074534361-bb09a38cf917?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(ivBack);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastDay = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastDay.getJSONArray("hour");
                    for (int i = 0 ;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img  = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherRvModelArrayList.add(new WeatherRvModel(time,temp,img,wind));

                    }

                    weatherRvAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


}