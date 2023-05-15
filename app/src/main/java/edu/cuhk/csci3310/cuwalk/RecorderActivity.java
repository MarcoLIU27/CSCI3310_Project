package edu.cuhk.csci3310.cuwalk;

/**
 * @author williw23 DAIWeican
 */

import edu.cuhk.csci3310.cuwalk.sportRecord.SportRecordService;
import edu.cuhk.csci3310.cuwalk.sportRecord.Timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.List;


public class RecorderActivity extends AppCompatActivity implements OnMapReadyCallback {
    final String MenuFileName = "HistoryMenu";
    Button activeButton;
    TextView timeText;
    TextView distanceText;
    SupportMapFragment mapFragment;
    FragmentTransaction transaction;
    private GoogleMap mMap;
    private SportRecordService service;
    private boolean RecordEnabled = false;
    private Timer timer = null;
    private int latlng_pair_size = 0;

    private ArrayList<LatLng> path;
    private LatLng home;
    private Marker currentLocationMaker = null;
    private Polyline currentPolyline = null;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            SportRecordService.MyBinder myBinder = (SportRecordService.MyBinder) binder;
            service = myBinder.getService();
            Log.i("appInfo", "onServiceConnected");
            RecordEnabled = service.checkRecorderRunning();
            try {
                initializeMap();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("appInfo", "onServiceDisconnected");
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        if (path != null && path.size() > 1) {
            currentPolyline = mMap.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .width(5)
                    .color(Color.BLACK));
        }

        LatLng home = service.getCurrentLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));
        currentLocationMaker = mMap.addMarker(new MarkerOptions().position(home).title("Your Location"));

//        Log.i("appInfo","onMapReady end");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        startService(new Intent(getBaseContext(), SportRecordService.class));
        bindService(new Intent(this, SportRecordService.class), conn, BIND_AUTO_CREATE);

        mapFragment = SupportMapFragment.newInstance();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.map_record, mapFragment);
        transaction.commit();
        timeText = (TextView) findViewById(R.id.time);
        distanceText = (TextView) findViewById(R.id.distance);
        activeButton = (Button) findViewById(R.id.active_button);

        activeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RecordEnabled = service.checkRecorderRunning();
                if (RecordEnabled == false) {
                    service.startRecording();
                    timer = service.getTimer();
                    activeButton.setText("STOP");
                    updateInfo();
                } else {
                    service.stopRecorder();
                    unbindService(conn);
                    activeButton.setText("START");
                    stopService(new Intent(getBaseContext(), SportRecordService.class));
                    Log.i("appInfo", "------------------------");
                    startService(new Intent(getBaseContext(), SportRecordService.class));
                    bindService(new Intent(RecorderActivity.this, SportRecordService.class), conn, BIND_AUTO_CREATE);
                }
            }
        });

        Log.i("appInfo", "onCreate end");

    }

    private void updateInfo() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                path = new ArrayList<>(service.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mapFragment.getMapAsync(RecorderActivity.this);
                    }
                });
                long duration = 0;
                double distance = service.getDistance();
                latlng_pair_size = path.size();
                RecordEnabled = service.checkRecorderRunning();
                int service_side_size;
                while (RecordEnabled == true) {
                    service_side_size = service.getCurrentSize();
                    if (latlng_pair_size < service_side_size) {
                        distance = service.getDistance();
                        for (int i = latlng_pair_size; i < service_side_size; ++i) {
                            path.add(service.getSingleLocation(i));
                            LatLng point = path.get(i);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mapFragment.getMapAsync(RecorderActivity.this);
                                }
                            });
                        }
                        latlng_pair_size = service_side_size;
                        Log.i("appInfo", "current number " + latlng_pair_size);
                        Log.i("appInfo", "current distance " + distance);
                    }
                    duration = timer.getDuration();
                    int second = (int) (duration / 1000) % 60;
                    int minute = (int) (duration / 60000) % 60;
                    int hour = (int) duration / 3600000;
                    String timeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                    String distanceString = String.format("%.2f", distance).toString() + " m";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeText.setText(timeString);
                            distanceText.setText(distanceString);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    RecordEnabled = service.checkRecorderRunning();
                }
                if (latlng_pair_size > 1) {
                    long start_time = timer.getStartTime();
                    long end_time = start_time + duration;
                    String[] start_time_array = new Date(start_time).toString().split(" ");
                    String start_time_string = start_time_array[1] + " " + start_time_array[2] + " " + start_time_array[5] + " " + start_time_array[3];
                    String[] end_time_array = new Date(end_time).toString().split(" ");
                    String end_time_string = end_time_array[1] + " " + end_time_array[2] + " " + end_time_array[5] + " " + end_time_array[3];
//                    Log.i("appInfo",start_time_string);
//                    Log.i("appInfo",end_time_string);
                    try {
                        writeToLocal(start_time_string, end_time_string, duration, distance, path);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Looper.prepare();
                    Toast.makeText(RecorderActivity.this, "New record saved.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
//                    for(int i = 0; i < getBaseContext().fileList().length; ++i){
//                        Log.i("appInfo",getBaseContext().fileList()[i]);
//                    }

                    Looper.prepare();
                    Toast.makeText(RecorderActivity.this, "The route is too short.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
        t.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("appInfo", "onStart end");
    }

    private void initializeMap() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (service == null || service.getCurrentLocation() == null) {
                    if (service == null) {
                        Log.i("appInfo", "no service");
                    }
                    if (service.getCurrentLocation() == null) {
                        Log.i("appInfo", "null location");
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.i("appInfo", "out");
            }
        });
        t.start();
        t.join();
        mapFragment.getMapAsync(this);
        if (RecordEnabled == true) {
            activeButton.setText("STOP");
            timer = service.getTimer();
            updateInfo();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (RecordEnabled == false) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapFragment.getMapAsync(RecorderActivity.this);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void writeToLocal(String start_date, String end_date, long duration, double distance, ArrayList<LatLng> route) throws JSONException {
//        Log.i("appInfo", start_date);
//        Log.i("appInfo", end_date);
//        Log.i("appInfo", String.format("%d",duration/1000).toString());
//        Log.i("appInfo", String.format("%.2f",distance).toString());
        String filename = start_date.replace(":", "_").replace(" ", "_");
        Log.i("appInfo", filename);

        // update the history record
        String json_array_string = "";
        try {
            FileInputStream fis = this.openFileInput(MenuFileName);
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
                json_array_string = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            json_array_string = "[]";
            File file = new File(this.getFilesDir(), MenuFileName);
            try {
                file.createNewFile();
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        JSONArray history_array;
        try{
            history_array = new JSONArray(json_array_string);
        } catch (Exception e) {
            history_array = new JSONArray();
        }

        JSONObject single = new JSONObject();

        single.put("start_date", start_date);
        single.put("end_date", end_date);
        single.put("duration", String.format("%d", duration / 1000).toString());
        single.put("distance", String.format("%.2f", distance).toString());
        single.put("filename", filename);
        history_array.put(single);

        Log.i("appInfo",single.toString());
        Log.i("appInfo",history_array.toString());
        try (FileOutputStream fos = this.openFileOutput(MenuFileName, this.MODE_PRIVATE)) {
            fos.write(history_array.toString().getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // save the path
        File file = new File(this.getFilesDir(), filename);
        try {
            file.createNewFile();
        } catch (IOException err) {
            err.printStackTrace();
        }
        JSONObject pathObj = new JSONObject();
        JSONArray LatArray = new JSONArray();
        JSONArray LngArray = new JSONArray();
        for(LatLng point: path){
            LatArray.put(point.latitude);
            LngArray.put(point.longitude);
        }
        pathObj.put("lat",LatArray);
        pathObj.put("lng",LngArray);
        Log.i("appInfo",pathObj.toString());
        try (FileOutputStream fos = this.openFileOutput(filename, this.MODE_PRIVATE)) {
            fos.write(pathObj.toString().getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}