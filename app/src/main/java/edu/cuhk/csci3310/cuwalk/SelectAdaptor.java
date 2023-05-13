package edu.cuhk.csci3310.cuwalk;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SelectAdaptor extends RecyclerView.Adapter<SelectAdaptor.ViewHolder> {

    private ArrayList<String> mItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        private OnItemClickListener mListener;

        public ViewHolder(View v, OnItemClickListener listener) {
            super(v);
            mListener = listener;
            mTextView = v.findViewById(R.id.text_item);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(mTextView.getText().toString());
                Log.d("TAG", "onClick: " + mTextView.getText().toString());
            }
        }
    }

    public SelectAdaptor(SelectPointActivity selectPointActivity, ArrayList<String> items) {
        mItems = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectlist_item, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

