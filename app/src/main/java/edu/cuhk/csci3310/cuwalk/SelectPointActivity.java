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
        // Todo:
        mItems.add("MTR Station");
        mItems.add("Lee Woo Sing College");
        mItems.add("Chan Chun Ha Hall - G/F");
        mItems.add("Chan Chun Ha Hall - 10/F");
        mItems.add("United College");
        mItems.add("New Asia College");

        mItems.add("Lee Shaw Kee Building - 3/F");
        mItems.add("Lee Shaw Kee Building - UG/F");
        mItems.add("University Library");
        mItems.add("Science Center");
        mItems.add("Mong Man Wai Building - 7/F");

        mItems.add("Mong Man Wai Building - G/F");
        mItems.add("Shaw College");
        mItems.add("Fusion");
        mItems.add("Engineer Building - 9/F");
        mItems.add("Engineer Building - 4/F");

        mItems.add("Engineer Building - G/F");
        mItems.add("University Gym");
        mItems.add("Chung Chi College");
        mItems.add("Wu Ho Man Yuen Building");
        mItems.add("Yasumoto Internation Academic Park");

        mItems.add("Pommerenke Student Centre");
        mItems.add("Chung Chi Teaching Buildings");

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


