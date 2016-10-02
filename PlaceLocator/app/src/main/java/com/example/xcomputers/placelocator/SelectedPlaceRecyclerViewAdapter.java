package com.example.xcomputers.placelocator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by svetlio on 1.10.2016 г..
 */

public class SelectedPlaceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public MyRecyclerViewAdapter.MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    class SelectedPlaceViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView description;
        public SelectedPlaceViewHolder(View itemView) {
            super(itemView);
         //   icon =
        }
    }
}

