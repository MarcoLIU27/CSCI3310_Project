package edu.cuhk.csci3310.cuwalk;

/**
 * @author williw23 DAIWeican
 */

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;


public class HistoryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView dateText;
    TextView timeText;
    TextView distanceText;
    SupportMapFragment mapFragment;
    FragmentTransaction transaction;
    private GoogleMap mMap;
    private int position = -1;
    private String file_name = "";
    private ArrayList<LatLng> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        mapFragment = SupportMapFragment.newInstance();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.map_record, mapFragment);
        transaction.commit();

        dateText = (TextView) findViewById(R.id.date);
        timeText = (TextView) findViewById(R.id.time);
        distanceText = (TextView) findViewById(R.id.distance);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("json_info");

        JSONObject record;
        String start_date, end_date;
        long duration = 0;
        double distance = 0;
        try {
            record = new JSONObject(jsonString);
            start_date = record.getString("start_date");
            end_date = record.getString("end_date");
            duration = record.getLong("duration");
            distance = record.getDouble("distance");
            file_name = record.getString("filename");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        dateText.setText(start_date + " - " + end_date);
        int second = (int) duration % 60;
        int minute = (int) (duration / 60) % 60;
        int hour = (int) duration / 3600;
        String timeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        String distanceString = String.format("%.2f", distance).toString() + " m";
        timeText.setText(timeString);
        distanceText.setText(distanceString);

        path = new ArrayList<>();
        String json_string = "";
        try {
            FileInputStream fis = this.openFileInput(file_name);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
            } finally {
                json_string = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        try {
            JSONObject pathObj = new JSONObject(json_string);
            JSONArray lat = pathObj.getJSONArray("lat");
            JSONArray lng = pathObj.getJSONArray("lng");
            for(int i = 0; i < lat.length(); ++i){
                path.add(new LatLng(lat.getDouble(i),lng.getDouble(i)));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(path.get(0))
                .title("start point")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet("START"));
        mMap.addMarker(new MarkerOptions().position(path.get(path.size()-1))
                .title("end point")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet("END"));
        // Draw the line first
        mMap.addPolyline(new PolylineOptions()
                .addAll(path)
                .width(5)
                .color(Color.BLACK));
        // Calculate the bounds and move the camera.
        LatLngBounds bounds = null;
        for (LatLng point : path) {
            if (bounds == null) {
                bounds = new LatLngBounds(point, point);
            } else {
                bounds = bounds.including(point);
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels - 500,
                100));

    }
}