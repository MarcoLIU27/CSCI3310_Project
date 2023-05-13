package edu.cuhk.csci3310.cuwalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectPointActivity extends AppCompatActivity {

    private ArrayList<String> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //setContentView(R.layout.test);

        //TextView textView =  findViewById(R.id.textView);

        // populate the list with some items
        // Todo: 目前是hardcode
        mItems.add("Item 1");
        mItems.add("Item 2");
        mItems.add("Item 3");
        mItems.add("Item 4");
        mItems.add("Item 5");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SelectAdaptor(this, mItems));

        // set up the click listener for the RecyclerView items
        SelectAdaptor.OnItemClickListener listener = new SelectAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                Intent data = new Intent();
                data.putExtra("selected_item", item);
                setResult(Activity.RESULT_OK, data);
                String selected = data.getStringExtra("selected_item");
                Log.d("SELECT", "Click: " + selected);
                finish();
            }
        };
        ((SelectAdaptor) recyclerView.getAdapter()).setOnItemClickListener(listener);
    }
}


