package edu.cuhk.csci3310.cuwalk;
//
// Name: Haoyu Liu
// SID: 1155141556
//
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CUWalk";
    private final String mAppFilePath = "/data/data/edu.cuhk.csci3310.cublossom/";
    private GoogleMap mMap;
    private RequestQueue mRequestQueue;
    private final String url = "https://www.cse.cuhk.edu.hk/~ypchui/csci3310/asg3/walk2.json";
    // TODO: add other attributes as needed
    private boolean isFragmentDisplayed = false;
    private Polyline polyline;
    static final String STATE_FRAGMENT = "false";

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
        mMap.addMarker(new MarkerOptions().position(home).title("Marker in CUHK"));
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
            int distance = jsonObject.getInt("distance");
            String gender = jsonObject.getString("gender");
            int height = jsonObject.getInt("height");
            String pace = jsonObject.getString("pace");
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


            // Zoom camera to enclose the path and its padding
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : path) {
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            int padding = 100;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cameraUpdate);

            //approximate distance based the length of the path segments
            if (distance == 0){
                int numPoints = latitudes.length();
                double totalDistance = 0.0;

                for (int i = 1; i < numPoints; i++) {
                    double lat1 = latitudes.getDouble(i-1);
                    double lon1 = longitudes.getDouble(i-1);
                    double lat2 = latitudes.getDouble(i);
                    double lon2 = longitudes.getDouble(i);

                    double R = 6371e3; // Earth's radius in meters
                    double phi1 = Math.toRadians(lat1);
                    double phi2 = Math.toRadians(lat2);
                    double deltaPhi = Math.toRadians(lat2-lat1);
                    double deltaLambda = Math.toRadians(lon2-lon1);

                    double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                            Math.cos(phi1) * Math.cos(phi2) *
                                    Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

                    double segmentDistance = R * c;
                    totalDistance += segmentDistance;
                }
                distance = (int) totalDistance;
            }

            // Create a fragment of ProfileFragment class and display details on it
            //displayFragment(distance, gender, height, pace);

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
        ProfileFragment profileFragment = (ProfileFragment) fragmentManager
                .findFragmentById(R.id.fragment_container);
        if (profileFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(profileFragment).commit();
        }
        // remove the path plotted
        polyline.remove();
        // Set boolean flag to indicate fragment is closed.
        isFragmentDisplayed = false;
    }
}