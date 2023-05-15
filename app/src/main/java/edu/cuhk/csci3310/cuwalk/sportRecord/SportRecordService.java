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

import edu.cuhk.csci3310.cuwalk.RecorderActivity;

/**
 * @author williw23 DAIWeican
 */
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
    private double distance = 0;
    // control the frequency of requesting location
    private int update_interval = 2000;
    private float update_min_distance_change = 1;
    private int latlng_pair_size = 0;
    // a flag to tell the activity that the info is a new appended location or the whole route
    // the flag is true when the activity is resumed.
    private boolean whole_route = true;
    private boolean RecordEnabled = false;
    // timer is used to record the passed time
    private Timer timer;

    // the binder is used for requesting detail info of the route
    private MyBinder binder = new MyBinder();
    private LocationManager lm, lmForSingleRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lmForSingleRequest = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lmForSingleRequest.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                update_interval, update_min_distance_change, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {}
                });
        Log.i("appInfo", "service created");
//        startRecording();
    }
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
        timer = new Timer();
        if (RecordEnabled == false) {
            RecordEnabled = true;
            Log.i("appInfo", "service startRecording");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("appInfo", "no location permission");
                return;
            }

            // ask for the location information in each second
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    update_interval, update_min_distance_change, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            currentLng = location.getLongitude();
                            currentLat = location.getLatitude();
                            path.add(new LatLng(currentLat, currentLng));
                            ++latlng_pair_size;
                            if(latlng_pair_size > 1){
                                distance += calculateDistance(path.get(latlng_pair_size - 1), path.get(latlng_pair_size-2));
                            }
                        }
                    });
            timer.startTimer();
        }

    }

    public boolean checkRecorderRunning() {
//        Log.i("appInfo", "The recorder running state is " + RecordEnabled);
        return RecordEnabled;
    }

    public void stopRecorder(){
        RecordEnabled = false;
    }

    public List<LatLng> getPath() {
        return path;
    }

    public LatLng getSingleLocation(int index) {
        return path.get(index);
    }

    public double getDistance() {
        return distance;
    }

    public Timer getTimer() {
        return timer;
    }

    public int getCurrentSize() {
        return latlng_pair_size;
    }

    public LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try{
            Location location = lmForSingleRequest.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return new LatLng(location.getLatitude(), location.getLongitude());
        }catch(Exception e){
            return null;
        }
    }

    public double calculateDistance(LatLng pt1, LatLng pt2) {
        double lat1 = pt1.latitude;
        double lng1 = pt1.longitude;
        double lat2 = pt2.latitude;
        double lng2 = pt2.longitude;
        double R = 6371000; // metres
        double phi1 = lat1 * Math.PI / 180; // φ, λ in radians
        double phi2 = lat2 * Math.PI / 180;
        double deltaPhi = (lat2 - lat1) * Math.PI / 180;
        double deltaLambda = (lng2 - lng1) * Math.PI / 180;
        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // in metres
        return d;
    }

}

