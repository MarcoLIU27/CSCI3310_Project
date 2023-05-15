package edu.cuhk.csci3310.cuwalk;

import edu.cuhk.csci3310.cuwalk.sportRecord.SportRecordService;
import edu.cuhk.csci3310.cuwalk.sportRecord.Timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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

import java.util.ArrayList;
import java.util.List;

public class RecorderActivity extends AppCompatActivity implements OnMapReadyCallback {

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
        if(currentLocationMaker != null){
            currentLocationMaker.remove();
        }
        if(currentPolyline != null){
            currentPolyline.remove();
        }
        if(path != null && path.size() > 1){
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

        activeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                RecordEnabled = service.checkRecorderRunning();
                if(RecordEnabled == false){
                    service.startRecording();
                    timer = service.getTimer();
                    activeButton.setText("STOP");
                    updateInfo();
                } else {
                    service.stopRecorder();
                    unbindService(conn);
                    activeButton.setText("START");
                    stopService(new Intent(getBaseContext(), SportRecordService.class));
                    Log.i("appInfo","------------------------");
                    startService(new Intent(getBaseContext(), SportRecordService.class));
                    bindService(new Intent(RecorderActivity.this, SportRecordService.class), conn, BIND_AUTO_CREATE);
                }
            }
        });

        Log.i("appInfo","onCreate end");

    }

    private void updateInfo(){
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

                double distance = service.getDistance();
                latlng_pair_size = path.size();
                RecordEnabled = service.checkRecorderRunning();
                int service_side_size;
                while (RecordEnabled == true) {
                    service_side_size = service.getCurrentSize();
                    if(latlng_pair_size < service_side_size){
                        distance = service.getDistance();
                        for(int i = latlng_pair_size; i < service_side_size; ++i){
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
                    long duration = timer.getDuration()/1000;
                    int second = (int) duration%60;
                    int minute =  (int) (duration/60) % 60;
                    int hour = (int) duration/3600;
                    String timeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                    String distanceString = String.format("%.2f",distance).toString()+" m";
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
                if(latlng_pair_size > 1){
                    // TODO
                    // handle the record
                    Looper.prepare();
                    Toast.makeText(RecorderActivity.this, "New record saved", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
        t.start();
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.i("appInfo","onStart end");
    }
    private void initializeMap() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(service == null || service.getCurrentLocation() == null){
                    Log.i("appInfo","null location");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.i("appInfo","out");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
        t.join();
        mapFragment.getMapAsync(this);
        if(RecordEnabled == true){
            activeButton.setText("STOP");
            timer = service.getTimer();
            updateInfo();
        }
//        else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (RecordEnabled == false) {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        Log.i("appInfo",service.getCurrentLocation().toString());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mapFragment.getMapAsync(RecorderActivity.this);
//                            }
//                        });
//                    }
//                }
//            }).start();
//        }
    }
}