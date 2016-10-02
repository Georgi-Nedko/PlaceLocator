package com.example.xcomputers.placelocator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import io.techery.properratingbar.ProperRatingBar;

public class SelectedPlaceActivity extends AppCompatActivity {

    private HorizontalScrollView hsv;
    private ImageButton call;
    private RatingBar myRB;
    private ProperRatingBar cashRB;
    private TextView myRatingBarTV;
    private TextView placeNameTV;
    private TextView reviewsTV;
    private View categoriesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place);
        hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        call = (ImageButton) findViewById(R.id.callingButton);
        myRB = (RatingBar) findViewById(R.id.MyRatingBar);
        myRatingBarTV = (TextView) findViewById(R.id.MyRatingBarTV);
        placeNameTV = (TextView) findViewById(R.id.nameTextView);
        reviewsTV = (TextView) findViewById(R.id.reviewsTV);
        cashRB = (ProperRatingBar) findViewById(R.id.MyCashRatingBar);
        categoriesRecyclerViewAdapter = findViewById(R.id.recycler_view_selected_place);

//        categoriesRecyclerViewAdapter = (RecyclerView) findViewById(R.id.recycler_view);
//        List<Category> list = new ArrayList<>();
//        addAllCategories(list);
//
//        categoriesView.setLayoutManager(new LinearLayoutManager(this));
//        CategoriesRecyclerViewAdapter adapter = new CategoriesRecyclerViewAdapter(this, list);
//        categoriesView.setAdapter(adapter);
//        adapter.setOnItemClickListener(new CategoriesRecyclerViewAdapter.onItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                executeRequest(position);
//            }
//        });

        cashRB.setRating(2);
        myRB.setRating(2.8f);
        myRatingBarTV.setText(myRB.getRating() + "");

        //getting from json and put


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectedPlaceActivity.this, "I am calling you", Toast.LENGTH_SHORT).show();
            }
        });

       hsv.postDelayed(new Runnable() {
           @Override
           public void run() {
               hsv.smoothScrollTo(500,0);
           }
       },1000);
    }

}
