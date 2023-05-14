package edu.cuhk.csci3310.cuwalk;
//
// Name: Haoyu Liu
// SID: 1155141556
//
// Name: Yuanheng LI
// SID: 1155141669
//

import pathsearch.DijkstraSP;
import pathsearch.IndexMinPriorityQueue;
import pathsearch.EdgeWeightedDigraph;
import pathsearch.DirectedEdge;
import java.util.Deque;

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

import com.google.android.gms.maps.model.LatLng;

import edu.cuhk.csci3310.cuwalk.MapsActivity;

import java.util.ArrayList;
import java.util.Objects;


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
    LatLng latlng_lsk = new LatLng(22.419790153230732, 114.20395791170668);
    LatLng latlng_mmw = new LatLng(22.41971262166917, 114.20926385431979);
    LatLng latlng_cch = new LatLng(22.421907723921763, 114.20480660903495);
    LatLng latlng_erb = new LatLng(22.418134340970138, 114.20790757369053);

    private void plotPath(int a, int b)
    {
        if ( ((a==1)&&(b==2)) ||  ((a==2)&&(b==1)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk1");
        if ( ((a==2)&&(b==12)) ||  ((a==12)&&(b==2)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk2");
        if ( ((a==3)&&(b==4)) ||  ((a==4)&&(b==3)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk3");
        if ( ((a==6)&&(b==2)) ||  ((a==2)&&(b==6)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk4");
        if ( ((a==6)&&(b==4)) ||  ((a==4)&&(b==6)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk5");

        if ( ((a==4)&&(b==5)) ||  ((a==5)&&(b==4)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk6");
        if ( ((a==5)&&(b==10)) ||  ((a==10)&&(b==5)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk7");
        if ( ((a==9)&&(b==11)) ||  ((a==11)&&(b==9)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk8");
        if ( ((a==8)&&(b==9)) ||  ((a==9)&&(b==8)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk9");
        if ( ((a==8)&&(b==7)) ||  ((a==7)&&(b==8)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk10");

        if ( ((a==7)&&(b==13)) ||  ((a==13)&&(b==7)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk11");
        if ( ((a==13)&&(b==15)) ||  ((a==15)&&(b==13)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk12");
        if ( ((a==9)&&(b==14)) ||  ((a==14)&&(b==9)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk13");
        if ( ((a==15)&&(b==17)) ||  ((a==17)&&(b==15)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk14");
        if ( ((a==17)&&(b==18)) ||  ((a==18)&&(b==17)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk15");

        if ( ((a==16)&&(b==18)) ||  ((a==18)&&(b==16)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk16");
        if ( ((a==16)&&(b==22)) ||  ((a==22)&&(b==16)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk17");
        if ( ((a==22)&&(b==21)) ||  ((a==21)&&(b==22)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk18");
        if ( ((a==21)&&(b==20)) ||  ((a==20)&&(b==21)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk19");
        if ( ((a==18)&&(b==19)) ||  ((a==19)&&(b==18)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk20");

        if ( ((a==19)&&(b==20)) ||  ((a==20)&&(b==19)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk21");
        if ( ((a==20)&&(b==0)) ||  ((a==0)&&(b==20)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk22");
        if ( ((a==22)&&(b==0)) ||  ((a==0)&&(b==22)) )
            ((MapsActivity) getActivity()).executeJsonParseFromUri("walk23");
        // a is start point, b is end point
        // Frontend optional Todo: 根据特殊的a b的值显示额外内容，比如a b是同一座楼不同楼层，显示需要从几楼坐电梯到几楼
        //电梯ab如下: (6,7) (7,6) , (11,10) (10,11) , (2,3) (2,3) ,(14,15) (15,14) ,(14,16) (16,14) ,(15,16) (16,15)
        if ( ((a==6)&&(b==7)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_lsk , "Taking the Elevator", "Please take the elevator of Lee Shaw Kee Building from 3/F to UG/F");
        if ( ((a==7)&&(b==6)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_lsk , "Taking the Elevator", "Please take the elevator of Lee Shaw Kee Building from UG/F to 3/F");
        if ( ((a==11)&&(b==10)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_mmw , "Taking the Elevator", "Please take the elevator of Mong Man Wai Building from G/F to 7/F");
        if ( ((a==10)&&(b==11)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_mmw , "Taking the Elevator", "Please take the elevator of Lee Shaw Kee Building from 7/F to G/F");
        if ( ((a==2)&&(b==3)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_cch , "Taking the Elevator", "Please take the elevator of Chan Chun Ha Building from G/F to 10/F");
        if ( ((a==3)&&(b==2)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_cch , "Taking the Elevator", "Please take the elevator of Chan Chun Ha Building from 10/F to G/F");
        if ( ((a==14)&&(b==15)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_erb , "Taking the Elevator", "Please take the elevator of Engineering Building from 9/F to 4/F");
        if ( ((a==15)&&(b==14)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_erb , "Taking the Elevator", "Please take the elevator of Engineering Building from 4/F to 9/F");
        if ( ((a==14)&&(b==16)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_erb , "Taking the Elevator", "Please take the elevator of Engineering Building from 9/F to G/F");
        if ( ((a==16)&&(b==14)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_erb , "Taking the Elevator", "Please take the elevator of Engineering Building from G/F to 9/F");
        if ( ((a==15)&&(b==16)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_erb , "Taking the Elevator", "Please take the elevator of Engineering Building from 4/F to G/F");
        if ( ((a==16)&&(b==15)) )
            ((MapsActivity) getActivity()).executeAddMarker( latlng_erb , "Taking the Elevator", "Please take the elevator of Engineering Building from G/F to 4/F");
    }

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
                //
                // remove the path plotted
                if (MapsActivity.isPolylineDisplayed) {
                    ((MapsActivity) requireActivity()).removePolyline();
                }

                EdgeWeightedDigraph g = new EdgeWeightedDigraph(23);
                DirectedEdge e1 = new DirectedEdge(1,2,1.25); DirectedEdge e2 = new DirectedEdge(2,1,0.75);
                DirectedEdge e3 = new DirectedEdge(12,2,6.75); DirectedEdge e4 = new DirectedEdge(2,12,6.25);
                DirectedEdge e5 = new DirectedEdge(3,4,1.50); DirectedEdge e6 = new DirectedEdge(4,3,1.50);
                DirectedEdge e7 = new DirectedEdge(2,6,8.50); DirectedEdge e8 = new DirectedEdge(6,2,8.50);
                DirectedEdge e9 = new DirectedEdge(4,6,4.75); DirectedEdge e10 = new DirectedEdge(6,4,4.75);
                DirectedEdge e11 = new DirectedEdge(4,5,6.00); DirectedEdge e12 = new DirectedEdge(5,4,6.00);
                DirectedEdge e13 = new DirectedEdge(5,10,2.50); DirectedEdge e14 = new DirectedEdge(10,5,2.50);
                DirectedEdge e15 = new DirectedEdge(11,9,3.00); DirectedEdge e16 = new DirectedEdge(9,11,3.00);
                DirectedEdge e17 = new DirectedEdge(8,9,5.25); DirectedEdge e18 = new DirectedEdge(9,8,5.25);
                DirectedEdge e19 = new DirectedEdge(7,8,2.00); DirectedEdge e20 = new DirectedEdge(8,7,2.00);
                DirectedEdge e21 = new DirectedEdge(7,13,2.50); DirectedEdge e22 = new DirectedEdge(13,7,2.50);
                DirectedEdge e23 = new DirectedEdge(13,15,8.50); DirectedEdge e24 = new DirectedEdge(15,13,8.50);
                DirectedEdge e25 = new DirectedEdge(14,9,3.25); DirectedEdge e26 = new DirectedEdge(9,14,3.25);
                DirectedEdge e27 = new DirectedEdge(15,17,6.10); DirectedEdge e28 = new DirectedEdge(17,15,7.25);
                DirectedEdge e29 = new DirectedEdge(17,18,1.00); DirectedEdge e30 = new DirectedEdge(18,17,1.00);
                DirectedEdge e31 = new DirectedEdge(16,18,5.50); DirectedEdge e32 = new DirectedEdge(18,16,6.00);
                DirectedEdge e33 = new DirectedEdge(22,16,7.00); DirectedEdge e34 = new DirectedEdge(16,22,6.25);
                DirectedEdge e35 = new DirectedEdge(22,21,4.50); DirectedEdge e36 = new DirectedEdge(21,22,4.75);
                DirectedEdge e37 = new DirectedEdge(21,20,3.50); DirectedEdge e38 = new DirectedEdge(20,21,3.50);
                DirectedEdge e39 = new DirectedEdge(18,19,0.75); DirectedEdge e40 = new DirectedEdge(19,18,0.75);
                DirectedEdge e41 = new DirectedEdge(19,20,3.00); DirectedEdge e42 = new DirectedEdge(20,19,3.00);
                DirectedEdge e43 = new DirectedEdge(0,20,4.50); DirectedEdge e44 = new DirectedEdge(20,0,4.50);
                DirectedEdge e45 = new DirectedEdge(0,22,5.25); DirectedEdge e46 = new DirectedEdge(22,0,4.25);
                //elevators
                DirectedEdge e47 = new DirectedEdge(2,3,0.01); DirectedEdge e48 = new DirectedEdge(3,2,0.01);
                DirectedEdge e49 = new DirectedEdge(6,7,0.01); DirectedEdge e50 = new DirectedEdge(7,6,0.01);
                DirectedEdge e51 = new DirectedEdge(14,15,0.01); DirectedEdge e52 = new DirectedEdge(15,14,0.01);
                DirectedEdge e53 = new DirectedEdge(15,16,0.01); DirectedEdge e54 = new DirectedEdge(16,15,0.01);
                DirectedEdge e55 = new DirectedEdge(14,16,0.01); DirectedEdge e56 = new DirectedEdge(16,14,0.01);
                DirectedEdge e57 = new DirectedEdge(10,11,0.01); DirectedEdge e58 = new DirectedEdge(11,10,0.01);

                g.addEdge(e1);g.addEdge(e2);g.addEdge(e3);g.addEdge(e4);
                g.addEdge(e5);g.addEdge(e6);g.addEdge(e7);g.addEdge(e8);
                g.addEdge(e9);g.addEdge(e10);g.addEdge(e11);g.addEdge(e12);
                g.addEdge(e13);g.addEdge(e14);g.addEdge(e15);

                g.addEdge(e16);g.addEdge(e17);g.addEdge(e18);g.addEdge(e19);
                g.addEdge(e20);g.addEdge(e21);g.addEdge(e22);g.addEdge(e23);
                g.addEdge(e24);g.addEdge(e25);g.addEdge(e26);g.addEdge(e27);
                g.addEdge(e28);g.addEdge(e29);g.addEdge(e30);

                g.addEdge(e31);g.addEdge(e32);g.addEdge(e33);g.addEdge(e34);
                g.addEdge(e35);g.addEdge(e36);g.addEdge(e37);g.addEdge(e38);
                g.addEdge(e39);g.addEdge(e40);g.addEdge(e41);g.addEdge(e42);
                g.addEdge(e43);g.addEdge(e44);g.addEdge(e45);g.addEdge(e46);

                g.addEdge(e47);g.addEdge(e48);
                g.addEdge(e49);g.addEdge(e50);g.addEdge(e51);g.addEdge(e52);
                g.addEdge(e53);g.addEdge(e54);g.addEdge(e55);g.addEdge(e56);
                g.addEdge(e57);g.addEdge(e58);

                int startNo = -1;
                int endNo = -1;

                if(Objects.equals(mSelectedStart, "MTR Station"))
                    startNo = 0;
                else if(Objects.equals(mSelectedStart, "Lee Woo Sing College"))
                    startNo = 1;
                else if(Objects.equals(mSelectedStart, "Chan Chun Ha Hall - G/F"))
                    startNo = 2;
                else if(Objects.equals(mSelectedStart, "Chan Chun Ha Hall - 10/F"))
                    startNo = 3;
                else if(Objects.equals(mSelectedStart, "United College"))
                    startNo = 4;
                else if(Objects.equals(mSelectedStart, "New Asia College"))
                    startNo = 5;
                else if(Objects.equals(mSelectedStart, "Lee Shaw Kee Building - 3/F"))
                    startNo = 6;
                else if(Objects.equals(mSelectedStart, "Lee Shaw Kee Building - UG/F"))
                    startNo = 7;
                else if(Objects.equals(mSelectedStart, "University Library"))
                    startNo = 8;
                else if(Objects.equals(mSelectedStart, "Science Center"))
                    startNo = 9;
                else if(Objects.equals(mSelectedStart, "Mong Man Wai Building - 7/F"))
                    startNo = 10;
                else if(Objects.equals(mSelectedStart, "Mong Man Wai Building - G/F"))
                    startNo = 11;
                else if(Objects.equals(mSelectedStart, "Shaw College"))
                    startNo = 12;
                else if(Objects.equals(mSelectedStart, "Fusion"))
                    startNo = 13;
                else if(Objects.equals(mSelectedStart, "Engineer Building - 9/F"))
                    startNo = 14;
                else if(Objects.equals(mSelectedStart, "Engineer Building - 4/F"))
                    startNo = 15;
                else if(Objects.equals(mSelectedStart, "Engineer Building - G/F"))
                    startNo = 16;
                else if(Objects.equals(mSelectedStart, "University Gym"))
                    startNo = 17;
                else if(Objects.equals(mSelectedStart, "Chung Chi College"))
                    startNo = 18;
                else if(Objects.equals(mSelectedStart, "Wu Ho Man Yuen Building"))
                    startNo = 19;
                else if(Objects.equals(mSelectedStart, "Yasumoto Internation Academic Park"))
                    startNo = 20;
                else if(Objects.equals(mSelectedStart, "Pommerenke Student Centre"))
                    startNo = 21;
                else if(Objects.equals(mSelectedStart, "Chung Chi Teaching Buildings"))
                    startNo = 22;

                if(Objects.equals(mSelectedEnd, "MTR Station"))
                    endNo = 0;
                else if(Objects.equals(mSelectedEnd, "Lee Woo Sing College"))
                    endNo = 1;
                else if(Objects.equals(mSelectedEnd, "Chan Chun Ha Hall - G/F"))
                    endNo = 2;
                else if(Objects.equals(mSelectedEnd, "Chan Chun Ha Hall - 10/F"))
                    endNo = 3;
                else if(Objects.equals(mSelectedEnd, "United College"))
                    endNo = 4;
                else if(Objects.equals(mSelectedEnd, "New Asia College"))
                    endNo = 5;
                else if(Objects.equals(mSelectedEnd, "Lee Shaw Kee Building - 3/F"))
                    endNo = 6;
                else if(Objects.equals(mSelectedEnd, "Lee Shaw Kee Building - UG/F"))
                    endNo = 7;
                else if(Objects.equals(mSelectedEnd, "University Library"))
                    endNo = 8;
                else if(Objects.equals(mSelectedEnd, "Science Center"))
                    endNo = 9;
                else if(Objects.equals(mSelectedEnd, "Mong Man Wai Building - 7/F"))
                    endNo = 10;
                else if(Objects.equals(mSelectedEnd, "Mong Man Wai Building - G/F"))
                    endNo = 11;
                else if(Objects.equals(mSelectedEnd, "Shaw College"))
                    endNo = 12;
                else if(Objects.equals(mSelectedEnd, "Fusion"))
                    endNo = 13;
                else if(Objects.equals(mSelectedEnd, "Engineer Building - 9/F"))
                    endNo = 14;
                else if(Objects.equals(mSelectedEnd, "Engineer Building - 4/F"))
                    endNo = 15;
                else if(Objects.equals(mSelectedEnd, "Engineer Building - G/F"))
                    endNo = 16;
                else if(Objects.equals(mSelectedEnd, "University Gym"))
                    endNo = 17;
                else if(Objects.equals(mSelectedEnd, "Chung Chi College"))
                    endNo = 18;
                else if(Objects.equals(mSelectedEnd, "Wu Ho Man Yuen Building"))
                    endNo = 19;
                else if(Objects.equals(mSelectedEnd, "Yasumoto Internation Academic Park"))
                    endNo = 20;
                else if(Objects.equals(mSelectedEnd, "Pommerenke Student Centre"))
                    endNo = 21;
                else if(Objects.equals(mSelectedEnd, "Chung Chi Teaching Buildings"))
                    endNo = 22;

                Log.d("SELECT", "startNo: " + startNo + ", endNo: " + endNo);

               //((MapsActivity) getActivity()).executeJsonParseFromUri("walk1");

                //根据图g和顶点，构建PrimMST对象
                DijkstraSP dsp = new DijkstraSP(g, startNo);
                //获取起点到终点的最短路径
                Deque<DirectedEdge> edges = dsp.pathTo(endNo);
                //打印输出
                for (DirectedEdge edge : edges) {
                    System.out.println(edge.from() + "->" + edge.to() + ":" + edge.weight());

                    plotPath(edge.from(), edge.to());
                }
                MapsActivity.isPolylineDisplayed = true;
            }
        });


        return view;
    }

}
