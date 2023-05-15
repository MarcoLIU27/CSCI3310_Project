package edu.cuhk.csci3310.cuwalk;

/**
 * @author williw23 DAIWeican
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordListAdapter extends Adapter<RecordListAdapter.RecordViewHolder>  {
    private Context context;
    private LayoutInflater mInflater;

    private final LinkedList<String> mRecordInfosList;
    class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, timeText, distanceText;
        final RecordListAdapter mAdapter;
        public RecordViewHolder(View itemView, RecordListAdapter adapter) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date);
            timeText = itemView.findViewById(R.id.time);
            distanceText = itemView.findViewById(R.id.distance);
            this.mAdapter = adapter;

            dateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("appInfo","dateText click");
                    int position = getLayoutPosition();
                    String jsonString = mRecordInfosList.get(position);
                    Intent intent = new Intent(v.getContext(),HistoryDetailActivity.class);
                    intent.putExtra("position_info", position);
                    intent.putExtra("json_info", jsonString);
                    v.getContext().startActivity(intent);
                }
            });
            timeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("appInfo","timeText click");
                    int position = getLayoutPosition();
                    String jsonString = mRecordInfosList.get(position);
                    Intent intent = new Intent(v.getContext(),HistoryDetailActivity.class);
                    intent.putExtra("position_info", position);
                    intent.putExtra("json_info", jsonString);
                    v.getContext().startActivity(intent);
                }
            });
            distanceText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("appInfo","distanceText click");
                    int position = getLayoutPosition();
                    String jsonString = mRecordInfosList.get(position);
                    Intent intent = new Intent(v.getContext(),HistoryDetailActivity.class);
                    intent.putExtra("position_info", position);
                    intent.putExtra("json_info", jsonString);
                    v.getContext().startActivity(intent);
                }
            });
            // End of ViewHolder initialization
        }
    }

    public RecordListAdapter(Context context,
                             LinkedList<String> infoList) {
        mInflater = LayoutInflater.from(context);
        this.mRecordInfosList = infoList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recordlist_item, parent, false);
        return new RecordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        JSONObject record = null;
        String start_date = "temp";
        String end_date = "temp";
        long duration = 0;
        double distance = 0;

        try {
            record = new JSONObject(mRecordInfosList.get(position));
            start_date = record.getString("start_date");
            end_date = record.getString("end_date");
            duration = record.getLong("duration");
            distance = record.getDouble("distance");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Update the following to display correct information based on the given position
        // Set up View items for this row (position), modify to show correct information read from the CSV
        holder.dateText.setText(start_date + " - " + end_date);
        int second = (int) duration % 60;
        int minute = (int) (duration / 60) % 60;
        int hour = (int) duration / 3600;
        String timeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        String distanceString = String.format("%.2f", distance).toString() + " m";
        holder.timeText.setText("Time: " + timeString);
        holder.distanceText.setText("Distance: " + distanceString);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mRecordInfosList.size();
    }

}