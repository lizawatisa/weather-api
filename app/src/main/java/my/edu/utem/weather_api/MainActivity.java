package my.edu.utem.weather_api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CustomAdapter adapter;
    EditText cityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);

        adapter = new CustomAdapter(getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    public void retrievePress(View view) {
        String city = cityEditText.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/forecast/daily?q="+city+",My&appid=9fd7a449d055dba26a982a3220f32aa2";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("debug", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response); //transform string ke JSON object
                            JSONArray weatherArray = jsonObject.getJSONArray("list"); //list ialah json dari api

                            for (int i=0; i<weatherArray.length(); i++){
                                adapter.addWeather(weatherArray.getJSONObject(i));
                            }

                            //refresh recyclereView
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        queue.add(stringRequest);

    }


   public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView weatherImageView;
        TextView weatherTextView, dateTextView, temperatureTextView;
        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.custom_row, parent, false)); //buka fail custom_row
            weatherTextView = itemView.findViewById(R.id.weatherTextView); //mesti itemView.findViewById
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weatherImageView = itemView.findViewById(R.id.weatherImage);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        List<JSONObject> weatherList = new ArrayList<>();
        Context context;

        public CustomAdapter(Context context){
            this.context = context;  //context ialah actvt..actvt mana gambar nk reload

        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()),viewGroup); //sama
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            JSONObject currentWeather = weatherList.get(position);

            try {
                holder.dateTextView.setText(""+currentWeather.getInt("dt"));
                holder.temperatureTextView.setText((currentWeather.getJSONObject("temp").getDouble("day")-273)+ " C");
                holder.weatherTextView.setText(currentWeather.getJSONArray("weather").getJSONObject(0).getString("main"));
                String iconId = currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconURL = "https://openweathermap.org/img/w/"+iconId+".png";
                Glide.with(MainActivity.this).load(iconURL).into(holder.weatherImageView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return weatherList.size();
        }

        public void addWeather (JSONObject weather){
            weatherList.add(weather); //dynamic add
        }
    }
}