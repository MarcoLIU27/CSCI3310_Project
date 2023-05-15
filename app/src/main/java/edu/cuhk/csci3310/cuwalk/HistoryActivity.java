package edu.cuhk.csci3310.cuwalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * @author williw23 DAIWeican
 */
public class HistoryActivity extends AppCompatActivity {
    final String MenuFileName = "HistoryMenu";
    private RecyclerView mRecyclerView;
    private RecordListAdapter mAdapter;
    private LinkedList<String> mRecordInfosList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initially put random data into the image list, modify to pass correct info read from JSON
        try {
            updateWholePage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.record_recycle_view);
        // Create an adapter and supply the data to be displayed,
        // initially just a list of image path
        // TODO: Update and pass more information as needed
        mAdapter = new RecordListAdapter(this, mRecordInfosList);

        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clean_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.clean_all_records:
                //delete files
                String[] filenames = new String[getBaseContext().fileList().length];
                for(int i = 0; i < getBaseContext().fileList().length; ++i){
                    filenames[i] = getBaseContext().fileList()[i];
                }
                for(int i = 0; i < filenames.length; ++i){
                    getBaseContext().deleteFile(filenames[i]);
                }
                try {
                    updateWholePage();
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                return true;
        }
        return true;
    }

    private void updateWholePage() throws JSONException {
        mRecordInfosList.clear();
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
            Log.i("appInfo", json_array_string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONArray history_array;
        try{
            history_array = new JSONArray(json_array_string);
        } catch (Exception e){
            history_array = new JSONArray();
        }


        // Convert the json array into a string array.
        for(int i = 0; i < history_array.length(); ++i){
            mRecordInfosList.addLast(history_array.getJSONObject(i).toString());
        }
    }


    @Override
    protected void onResume() {
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
//

        Log.d("appInfo", "onDestroy");
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Intent intent = new Intent(this,HistoryDetailActivity.class);
//        this.startActivity(intent);
//    }
}