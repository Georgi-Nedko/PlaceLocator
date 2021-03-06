package com.example.xcomputers.placelocator;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.xcomputers.placelocator.model.MyPlace;

import java.util.List;

/**
 * Created by xComputers on 30.9.2016 г..
 */

public class SearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.MyRecyclerViewHolder> {

    private List<MyPlace> places;
    private Activity activity;
    private onResultClickListener resultsItemClickListener;
    private Typeface custom_font;
    private Typeface custom_font_bold;

    public SearchResultsRecyclerViewAdapter(Activity activity, List<MyPlace> places) {
        this.places = places;
        this.activity = activity;
        custom_font = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Light.ttf");
        custom_font_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Bold.ttf");
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.places_list_view_item, parent, false);
        //create vh
        MyRecyclerViewHolder vh = new MyRecyclerViewHolder(row);
        //return vh
        return vh;

    }

    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, int position) {

        MyPlace place = places.get(position);
        holder.rating.setRating(place.getRating());
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());
        holder.distanceToPhone.setText(place.getDistanceToPhone());

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView address;
        RatingBar rating;
        TextView distanceToPhone;

        public MyRecyclerViewHolder(View itemView) {
            super(itemView);
            rating = (RatingBar) itemView.findViewById(R.id.place_rating);
            name = (TextView) itemView.findViewById(R.id.place_name);
            address = (TextView) itemView.findViewById(R.id.place_address);
            distanceToPhone = (TextView) itemView.findViewById(R.id.place_distance);
            name.setTypeface(custom_font_bold);
            address.setTypeface(custom_font);
            distanceToPhone.setTypeface(custom_font);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            resultsItemClickListener.onResultClicked(v, getPosition());
        }
    }

    public interface onResultClickListener {
        void onResultClicked(View view, int position);
    }

    public void setOnResultClickListener(final onResultClickListener mItemClickListener) {
        this.resultsItemClickListener = mItemClickListener;
    }
}
