package edu.cuhk.csci3310.cuwalk;
//
// Name: Haoyu Liu
// SID: 1155141556
//
import android.Manifest;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// Importing Arrays class from the utility class
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CUWalk";
    private final String mAppFilePath = "/data/data/edu.cuhk.csci3310.cublossom/";
    private GoogleMap mMap;
    private RequestQueue mRequestQueue;
    private final String url = "https://www.cse.cuhk.edu.hk/~ypchui/csci3310/asg3/walk2.json";
    // TODO: add other attributes as needed
    private boolean isFragmentDisplayed = false;
    public static boolean isPolylineDisplayed = false;
    private Polyline polyline;
    static final String STATE_FRAGMENT = "false";

    double maxLat = Double.NEGATIVE_INFINITY;
    double minLng = Double.POSITIVE_INFINITY;
    double minLat = Double.POSITIVE_INFINITY;
    double maxLng = Double.NEGATIVE_INFINITY;

    LatLng leftUpMost = null;
    LatLng rightBottomMost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // initialize a volley request queue for getting an online file
        mRequestQueue = Volley.newRequestQueue(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Add SearchFragment
        if (savedInstanceState == null) {
            displaySearchFragment();
        }
//        ActivityCompat.requestPermissions(this,
//                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
//                PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                PackageManager.PERMISSION_GRANTED);
//        startService(new Intent(getBaseContext(), SportRecordService.class));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        stopService(new Intent(getBaseContext(), SportRecordService.class));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker in CUHK.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Default marker and camera zoom, you don't have to modify the following
        // Add a marker in Campus and move the camera
        LatLng home = new LatLng(22.419871, 114.206169);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));
    }

    // TODO: override callbacks to create options menu for selecting routes
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.route_menu, menu);
        return true;
    }

 //   @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Change the map type based on the user's selection.
//        switch (item.getItemId()) {
//            case R.id.route1:
//                Log.d("myTag", "This is my message1");
//                // remove path & fragment if exists; parse url and display if not exists
//                if (isFragmentDisplayed) {
//                    closeFragment();
//                }
//                return true;
//            case R.id.route2:
//                Log.d("myTag", "This is my message2");
////                if (isFragmentDisplayed) {
////                    closeFragment();
////                }
////                jsonParseFromUri(url);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    /**
     *  TODO: implementation extra utility method to
     *  a. parse a json obtained from a local file, or
     *  b. parse a json obtained from server
     *  c. to draw a path and display step info
     *  d. other utilities such as Lat/Lng-distance conversion etc.
     */
    public void executeJsonParseFromUri(String url) {
        jsonParseFromUri(url);
    }

    private void jsonParseFromUri(String uri) {
        // TODO:
        //  a. parse a local file or
        //  b. start an online request with exception handling here
        try {
            // Try to parse a local file
            Log.d("myTag", "try open local");
            InputStream inputStream = getResources().openRawResource(
                    getResources().getIdentifier(uri, "raw", getPackageName())
            );
            Log.d("myTag", "local opened");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonString);
            Log.d("Response", jsonObject.toString());
            displayPathProfile(jsonObject);
        } catch (IOException | Resources.NotFoundException | JSONException e) {
            // If parsing the local file fails, start an online request
            Log.d("myTag", "try url");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, uri,null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Call the function to display the path profile
                            displayPathProfile(response);
                            Log.d("Response", response.toString());
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle the error here
                                    Log.e("Volley Error", error.toString());
                                }
                            }
                    );
            // Add the request to the RequestQueue
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            mRequestQueue.add(jsonObjectRequest);
        }
    }

    public void executeAddMarker(LatLng position, String title, String snippet){
        addMarker(position, title, snippet);
    }

    private void addMarker(LatLng position, String title, String snippet){
        // 创建标记选项
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet);

        // 添加标记到地图
        Marker marker = mMap.addMarker(markerOptions);

        // 设置信息窗口适配器到标记上
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // 创建信息窗口布局
                View view = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // 设置标题和内容
                TextView titleTextView = view.findViewById(R.id.titleTextView);
                titleTextView.setText(marker.getTitle());

                TextView snippetTextView = view.findViewById(R.id.snippetTextView);
                snippetTextView.setText(marker.getSnippet());

                return view;
            }
        });

        // 设置地图的标记点击监听器
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 显示信息窗口
                marker.showInfoWindow();
                return true;
            }
        });

    }

    private void displayPathProfile(JSONObject jsonObject) {
        // TODO:
        //  1. get the JSON fields, a try-catch block might be needed here
        //     a. textual fields for display/calculation
        //     b. Lat/Lng pairs for path drawing (via PolyLines)
        //        make suitable function calls to "mMap"
        //  2. zoom the map camera to enclose just the path and its padding
        //     Hints: use Arrays.sort for finding bound of path
        //  3. do distance & steps calculation
        //  4. create a fragment and display details on it
        try {
            // Get the JSON fields
            JSONArray start = jsonObject.getJSONArray("start");
            JSONArray end = jsonObject.getJSONArray("end");
            JSONArray latitudes = jsonObject.getJSONArray("Lat");
            JSONArray longitudes = jsonObject.getJSONArray("Lng");

            // Lat/Lng pairs for path drawing (via PolyLines)
            List<LatLng> path = new ArrayList<>();
            for (int i = 0; i < latitudes.length(); i++) {
                double lat = latitudes.getDouble(i);
                double lng = longitudes.getDouble(i);
                Log.d("Response", String.valueOf(lat));
                path.add(new LatLng(lat, lng));
            }

            // Draw Polyline
            PolylineOptions polylineOptions = new PolylineOptions().addAll(path);
            polyline = mMap.addPolyline(polylineOptions);
            Log.d("Response", "polylineOptions added");

            double lat = start.getDouble(0);
            double lng = start.getDouble(1);

            if (lat > maxLat || (lat == maxLat && lng < minLng)) {
                maxLat = lat;
                minLng = lng;
                leftUpMost = new LatLng(lat, lng);
            }

            if (lat < minLat || (lat == minLat && lng > maxLng)) {
                minLat = lat;
                maxLng = lng;
                rightBottomMost = new LatLng(lat, lng);
            }

            double lat1 = start.getDouble(0);
            double lng1 = start.getDouble(1);

            if (lat1 > maxLat || (lat1 == maxLat && lng1 < minLng)) {
                maxLat = lat1;
                minLng = lng1;
                leftUpMost = new LatLng(lat1, lng1);
            }

            if (lat1 < minLat || (lat1 == minLat && lng1 > maxLng)) {
                minLat = lat1;
                maxLng = lng1;
                rightBottomMost = new LatLng(lat1, lng1);
            }

            // Create LatLng objects from the start and end arrays
            LatLng startLatLng = leftUpMost;
            LatLng endLatLng = rightBottomMost;

            // Create a LatLngBounds object that includes the start and end points with padding
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(startLatLng)
                    .include(endLatLng)
                    .build();

            // Animate the camera to fit the LatLngBounds with padding
            int padding = 100;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cameraUpdate);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // TODO: override onBackPressed to handle a proper flow of app
    @Override
    public void onBackPressed() {
        // TODO: update fragment and map
        if (isFragmentDisplayed) {
            // remove fragment and update map
            closeFragment();
            LatLng home = new LatLng(22.419871, 114.206169);
            mMap.addMarker(new MarkerOptions().position(home).title("Marker in CUHK"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));
        } else {
            // quit app
            finish();
        }
    }

    // TODO: add other methods or callbacks as needed
    public void displayFragment(int distance, String gender, int height, String pace) {
        ProfileFragment profileFragment = ProfileFragment.newInstance(distance, gender, height, pace);
        Log.d("Fragment", "Init");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit();
        // Set boolean flag to indicate fragment is open.
        isFragmentDisplayed = true;
    }

    public void displaySearchFragment() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        Log.d("Fragment", "Init");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, searchFragment)
                .addToBackStack(null)
                .commit();
        // Set boolean flag to indicate fragment is open.
        isFragmentDisplayed = true;
    }

    public void closeFragment() {
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        SearchFragment searchFragment = (SearchFragment) fragmentManager
                .findFragmentById(R.id.fragment_container);
        if (searchFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(searchFragment).commit();
        }
        // remove the path plotted
        polyline.remove();

        // Set boolean flag to indicate fragment is closed.
        isFragmentDisplayed = false;
    }

    public void removePolyline() {
        // remove the path plotted
        //polyline.remove();
        mMap.clear(); // Clears all markers, polylines, and other shapes from the map.
        Log.d("REMOVE", "polyline removed ");
        maxLat = Double.NEGATIVE_INFINITY;
        minLng = Double.POSITIVE_INFINITY;
        minLat = Double.POSITIVE_INFINITY;
        maxLng = Double.NEGATIVE_INFINITY;

        leftUpMost = null;
        rightBottomMost = null;
        isPolylineDisplayed = false;
    }
}