package edu.cuhk.csci3310.cuwalk;
//
// Name: Haoyu Liu
// SID: 1155141556
//
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DISTANCE_PARAM = "distance_param";
    private static final String GENDER_PARAM = "gender_param";
    private static final String HEIGHT_PARAM = "height_param";
    private static final String PACE_PARAM = "pace_param";

    // TODO: Rename and change types of parameters
    private String mDistanceParam;
    private String mGenderParam;
    private String mHeightParam;
    private String mPaceParam;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param distance Parameter 1.
     * @param gender Parameter 2.
     * @param height Parameter 3.
     * @param pace Parameter 4.
     * @return A new instance of fragment BlankFragmentD.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int distance, String gender, int height, String pace) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(DISTANCE_PARAM, String.valueOf(distance));
        args.putString(GENDER_PARAM, gender);
        args.putString(HEIGHT_PARAM, String.valueOf(height));
        args.putString(PACE_PARAM, pace);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDistanceParam = getArguments().getString(DISTANCE_PARAM);
            mGenderParam = getArguments().getString(GENDER_PARAM);
            mHeightParam = getArguments().getString(HEIGHT_PARAM);
            mPaceParam = getArguments().getString(PACE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Fragment", "Created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO:
        //  1. get reference of the views and assign with received value
        final TextView textDistance = view.findViewById(R.id.textDistance);
        textDistance.setText("Distance:" + mDistanceParam);
        final TextView textGender = view.findViewById(R.id.textGender);
        textGender.setText("Gender:" + mGenderParam);
        final TextView textHeight = view.findViewById(R.id.textHeight);
        textHeight.setText("Height:" + mHeightParam);
        final TextView textPace = view.findViewById(R.id.textPace);
        textPace.setText("Pace:" + mPaceParam);

        //  2. To estimate the number of steps based on height (inches), pace (minutes per mile), and sex are as follows:
        double stepsPerMile = 0;
        int totalSteps = 0;
        int GenderParam = 0;
        int PaceParam = 0;
        final TextView textSteps = view.findViewById(R.id.textSteps);
        if (mGenderParam.equals("Female"))
            GenderParam = 1949;
        else
            GenderParam = 1916;
        if (mPaceParam.equals("Walk"))
            PaceParam = 9;
        if (mPaceParam.equals("Jog"))
            PaceParam = 12;

        stepsPerMile = GenderParam + 63.4 * PaceParam - 14.1 * Integer.parseInt(mHeightParam) / 2.54;
        totalSteps = (int) (stepsPerMile * Integer.parseInt(mDistanceParam) / 1609);
        textSteps.setText("Steps:" + String.valueOf(totalSteps));
    }
}