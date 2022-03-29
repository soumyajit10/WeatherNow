package com.example.weathernow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRvAdapter extends RecyclerView.Adapter<WeatherRvAdapter.WeatherRvViewHolder> {

    private Context context;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList;

    public WeatherRvAdapter(Context context, ArrayList<WeatherRvModel> weatherRvModelArrayList) {
        this.context = context;
        this.weatherRvModelArrayList = weatherRvModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRvViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        return new WeatherRvViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRvAdapter.WeatherRvViewHolder holder, int position) {

        WeatherRvModel model = weatherRvModelArrayList.get(position);
        holder.temperature.setText(model.getTemperature()+"°c");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.condition);
        holder.windSpeed.setText(model.getWindSpeed()+" Km/h");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = inputFormat.parse(model.getTime());
            holder.time.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList.size();
    }



    public class WeatherRvViewHolder extends RecyclerView.ViewHolder {

        private TextView time,windSpeed,temperature;
        private ImageView condition;

        public WeatherRvViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.weather_tvTime);
            windSpeed = itemView.findViewById(R.id.weather_tvWindSpeed);
            temperature = itemView.findViewById(R.id.weather_tvTemperature);
            condition = itemView.findViewById(R.id.weather_IvCondition);
        }
    }
}
