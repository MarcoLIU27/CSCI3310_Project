package edu.cuhk.csci3310.cuwalk;
//
// Name: Haoyu Liu
// SID: 1155141556
//

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import edu.cuhk.csci3310.cuwalk.MapsActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private TextView mTextStartView;
    private TextView mTextEndView;
    private String mStartParam = "Select a starting point";
    private String mEndParam = "Select a ending point";
    private String mSelectedStart;
    private String mSelectedEnd;

    private final ActivityResultLauncher<Intent> mStartSelectActivityForStartPoint = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selected = data.getStringExtra("selected_item");
                        Log.d("SELECT", "Click Return: " + selected);
                        mSelectedStart = selected;
                        mTextStartView.setText(selected);
                    }
                }
            });
    private final ActivityResultLauncher<Intent> mStartSelectActivityForEndPoint = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selected = data.getStringExtra("selected_item");
                        Log.d("SELECT", "Click Return: " + selected);
                        mSelectedEnd = selected;
                        mTextEndView.setText(selected);
                    }
                }
            });

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // find the TextView
        mTextStartView = view.findViewById(R.id.text_start);
        mTextEndView = view.findViewById(R.id.text_end);
        Button button = view.findViewById(R.id.search_button);
        mTextStartView.setText(mStartParam);
        mTextEndView.setText(mEndParam);
        mTextStartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent to start the new activity
                Intent intent = new Intent(getActivity(), SelectPointActivity.class);

                // start the list activity
                mStartSelectActivityForStartPoint.launch(intent);
                //startActivity(intent);
            }
        });
        mTextEndView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent to start the new activity
                Intent intent = new Intent(getActivity(), SelectPointActivity.class);

                // start the list activity
                mStartSelectActivityForEndPoint.launch(intent);
                //startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String url = "http://example.com/data.json";
                //Todo: 这里是hardcode，地图要可以放大缩小
                ((MapsActivity) getActivity()).executeJsonParseFromUri("walk1");
            }
        });


        return view;
    }

}
