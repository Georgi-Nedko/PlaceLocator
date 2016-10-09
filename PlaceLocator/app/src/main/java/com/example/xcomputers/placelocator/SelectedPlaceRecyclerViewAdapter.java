package com.example.xcomputers.placelocator;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.xcomputers.placelocator.model.Commentator;

import java.util.ArrayList;

/**
 * Created by svetlio on 1.10.2016 г..
 */

public class SelectedPlaceRecyclerViewAdapter extends RecyclerView.Adapter<SelectedPlaceRecyclerViewAdapter.MySelectedPlaceRecyclerViewVH> {
    private Activity activity;
    private ArrayList<Commentator> commentators;

    public SelectedPlaceRecyclerViewAdapter(Activity activity,ArrayList<Commentator> commentators) {
        this.activity = activity;
        this.commentators = commentators;
    }

    @Override
    public SelectedPlaceRecyclerViewAdapter.MySelectedPlaceRecyclerViewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.selected_place_list_view, parent, false);
        //create vh
        SelectedPlaceRecyclerViewAdapter.MySelectedPlaceRecyclerViewVH vh = new MySelectedPlaceRecyclerViewVH(row);
        //return vh
        return vh;

    }

    @Override
    public void onBindViewHolder(MySelectedPlaceRecyclerViewVH holder, int position) {
        Commentator commentator = commentators.get(position);
        holder.nameOfAuthor.setText(commentator.getName());
        holder.description.setText(commentator.getCommentDescription());
        holder.commentRating.setRating(commentator.getRating());
        holder.commentRatingTV.setText(""+ commentator.getRating());
    }


    @Override
    public int getItemCount() {
        return commentators.size();
    }


    protected class MySelectedPlaceRecyclerViewVH extends RecyclerView.ViewHolder {
        TextView nameOfAuthor;
        TextView description;
        TextView commentRatingTV;
        RatingBar commentRating;
        public MySelectedPlaceRecyclerViewVH(View itemView) {
            super(itemView);
            nameOfAuthor = (TextView) itemView.findViewById(R.id.nameOfCommentatorTV);
            description = (TextView) itemView.findViewById(R.id.descriptionTV);
            commentRatingTV = (TextView) itemView.findViewById(R.id.comentatorRatingTV);
            commentRating = (RatingBar) itemView.findViewById(R.id.ComentatorRatingBar);
        }
    }


//        @Override
//        public com.example.xcomputers.placelocator.CategoriesRecyclerViewAdapter.MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            switch (viewType) {
//                case 0: return new ViewHolder0(...);
//                case 2: return new ViewHolder2(...);
//                ...
//            }
//            LayoutInflater inflater = activity.getLayoutInflater();
//            View row = inflater.inflate(R.layout.categories_list_view_item, parent, false);
//            //create vh
//            com.example.xcomputers.placelocator.CategoriesRecyclerViewAdapter.MyRecyclerViewHolder vh = new com.example.xcomputers.placelocator.CategoriesRecyclerViewAdapter.MyRecyclerViewHolder(row);
//            //return vh
//            return vh;
//
//        }

}

