package com.example.xcomputers.placelocator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.xcomputers.placelocator.model.Category;

import java.util.List;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.MyRecyclerViewHolder> {
    private List<Category> categories;
    private Activity activity;
    private onItemClickListener mItemClickListener;
    Typeface custom_font;
    public CategoriesRecyclerViewAdapter(Activity activity, List<Category> categories){
        this.categories = categories;
        this.activity = activity;
        custom_font = Typeface.createFromAsset(activity.getAssets(), "fonts/Pacifico.ttf");
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.categories_list_view_item, parent, false);
        //create vh
        MyRecyclerViewHolder vh = new MyRecyclerViewHolder(row);
        //return vh
        return vh;

    }

    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, int position) {

        Category category = categories.get(position);
        holder.name.setText(category.getName());
        holder.image.setImageResource(category.getImage());


    }

    @Override
    public int getItemCount() {

        return categories.size();

    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        ImageView image;

        public MyRecyclerViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.category_image);
            name = (TextView) itemView.findViewById(R.id.category_name);
            name.setTypeface(custom_font);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition());
        }
    }
    public interface onItemClickListener{
         void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(final onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
