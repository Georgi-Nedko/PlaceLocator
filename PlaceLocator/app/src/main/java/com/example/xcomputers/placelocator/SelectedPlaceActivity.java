package com.example.xcomputers.placelocator;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import io.techery.properratingbar.ProperRatingBar;

public class SelectedPlaceActivity extends AppCompatActivity {

    private HorizontalScrollView hsv;
    private ImageButton call;
    private ImageButton website;
    private RatingBar myRB;
    private ProperRatingBar cashRB;
    private TextView myRatingBarTV;
    private TextView placeNameTV;
    private TextView reviewsTV;
    private TextView addressTV;
    private Spinner openTimeTV;
    private TextView phoneTV;
    private View categoriesRecyclerViewAdapter;
    private String placeSelectedID;
    private static String placeInfo;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place);
        placeSelectedID = getIntent().getStringExtra("json");
        //   Log.e("JSON",placeSelectedID);
        // Toast.makeText(SelectedPlaceActivity.this, placeSelectedID, Toast.LENGTH_SHORT).show();
        //TODO change the placeid with placeSelectedID
        RequestTask mytask = (RequestTask) new RequestTask().execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");

        //Toast.makeText(SelectedPlaceActivity.this, placeInfo, Toast.LENGTH_SHORT).show();



        hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        call = (ImageButton) findViewById(R.id.callingButton);
        website = (ImageButton) findViewById(R.id.websiteButton);
        myRB = (RatingBar) findViewById(R.id.MyRatingBar);
        myRatingBarTV = (TextView) findViewById(R.id.MyRatingBarTV);
        placeNameTV = (TextView) findViewById(R.id.nameTextView);
        reviewsTV = (TextView) findViewById(R.id.reviewsTV);
        addressTV = (TextView) findViewById(R.id.addressTV);
        openTimeTV = (Spinner) findViewById(R.id.open_time_spinner);
        phoneTV = (TextView) findViewById(R.id.phoneNumberTv);
        cashRB = (ProperRatingBar) findViewById(R.id.MyCashRatingBar);
        categoriesRecyclerViewAdapter = findViewById(R.id.recycler_view_selected_place);
//        uri = (TextView) findViewById(R.id.uriTV);



       // cashRB.setRating(2);
       // myRB.setRating(2.8f);
       // myRatingBarTV.setText(myRB.getRating() + "");

        //getting from json and put


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectedPlaceActivity.this, "website clicked", Toast.LENGTH_SHORT).show();
//                Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                callIntent.setData(Uri.parse("tel:" + phoneTV.getText().toString()));
//                startActivity(callIntent);
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SelectedPlaceActivity.this, "website clicked", Toast.LENGTH_SHORT).show();
                //Log.e("URI",uri);
                Toast.makeText(SelectedPlaceActivity.this, uri, Toast.LENGTH_SHORT).show();
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);


                            websiteIntent.setData(Uri.parse(uri));


                        startActivity(websiteIntent);
            }
        });

//        hsv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hsv.smoothScrollTo(500, 0);
//            }
//        }, 1000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int x = 1;
                while (true){
                    if (!hsv.canScrollHorizontally(x)){
                        x = -x;
                    }
                    final int finalX = x;
                    hsv.scrollBy(finalX, 0);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    class RequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = "";
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int status = connection.getResponseCode();
                Scanner sc = new Scanner(connection.getInputStream());
                while (sc.hasNextLine()) {
                    response += sc.nextLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
//            Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
//            intent.putExtra("json", s);
//            intent.putExtra("lastLocation", lastLocation);
//            startActivity(intent)
            try {
                JSONObject jObject = new JSONObject(s);
                final JSONObject jObjectResult = (JSONObject) jObject.get("result");
                phoneTV.setText(jObjectResult.getString("formatted_phone_number"));
                addressTV.setText(jObjectResult.getString("formatted_address"));
                placeNameTV.setText(jObjectResult.getString("name"));
                myRB.setRating((float) jObjectResult.getDouble("rating"));
                myRatingBarTV.setText(myRB.getRating()+"");
                JSONArray reviews = (jObjectResult.getJSONArray("reviews"));
                Log.e("REVIEWS" ,"" + reviews.length());
                reviewsTV.setText("" + reviews.length() + " reviews");
              //  Log.e("sadasd","dsaasddasasd");
                uri = jObjectResult.getString("website");

               // Log.e("URI",uri);
                cashRB.setRating((int) jObjectResult.getDouble("price_level"));
                Log.e("PRICE_LEVEL",jObjectResult.getInt("price_level") + "");



            }
            catch (JSONException e1) {
              //  Log.e("URI EXCEPTION",uri);
                cashRB.setRating(0);
                e1.printStackTrace();
            }


        }

    }
}


