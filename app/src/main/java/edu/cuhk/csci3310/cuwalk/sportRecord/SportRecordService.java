package edu.cuhk.csci3310.cuwalk.sportRecord;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SportRecordService extends Service {

    public class MyBinder extends Binder {
        public SportRecordService getService() {
            return SportRecordService.this;
        }
    }

    // Record the location
    private List<LatLng> path = new ArrayList<>();

    private double currentLat;
    private double currentLng;
    // control the frequency of requesting location
    private int update_interval = 1000;
    private float update_min_distance_change = 5;
    private int latlng_pair_size = 0;
    // a flag to tell the activity that the info is a new appended location or the whole route
    // the flag is true when the activity is resumed.
    private boolean whole_route = true;
    private boolean RecordEnabled = false;
    // timer is used to record the passed time
    private Timer timer = new Timer();

    // the binder is used for requesting detail info of the route
    private MyBinder binder = new MyBinder();
    private LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("appInfo", "onBind detected");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("appInfo", "onUnbind detected");
        return false;
    }

    public void startRecording() {
        if(RecordEnabled == false){
            RecordEnabled = true;
            Log.i("appInfo", "server startRecording");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("appInfo", "1" + PackageManager.PERMISSION_GRANTED);
                Log.i("appInfo", "no location permission");
                return;
            } else {
                Log.i("appInfo", "allowded");
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // ask for the location information in each second
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    update_interval, update_min_distance_change, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            currentLng = location.getLongitude();
                            currentLat = location.getLatitude();
                            path.add(new LatLng(currentLat, currentLng));
                            ++latlng_pair_size;
                        }

//                        @Override
//                        public void onProviderDisabled(String provider) {
//                        }
//
//                        @Override
//                        public void onProviderEnabled(String provider) {
//                        }
                    });
            timer.start();
        }

    }

    public boolean checkRecorderRunning() {
        return RecordEnabled;
    }

    public List<LatLng> getPath() {
        return path;
    }

    public LatLng getSingleLocation(int index) {
        return path.get(index);
    }

    public Timer getTimer() {
        return timer;
    }

}

