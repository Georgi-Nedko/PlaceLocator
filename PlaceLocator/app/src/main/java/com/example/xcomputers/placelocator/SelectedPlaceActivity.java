package com.example.xcomputers.placelocator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
    //private String placeSelectedID;
    private static String placeInfo;
    String uri;
    ImageView mImageView;
    ImageView mImageView2;
    HorizontalScrollView imagesHSV;
    ArrayList<Integer> images;
    int counterImages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place);

        //TODO change the placeid with placeSelectedID
       // RequestTask mytask = (RequestTask) new RequestTask().execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJoyoBvjx-j4ARNUWlMkjGUL4&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");

        //Toast.makeText(SelectedPlaceActivity.this, placeInfo, Toast.LENGTH_SHORT).show();
        String placeSelectedID = getIntent().getStringExtra("json");
        Log.e("JSON",placeSelectedID);
        Toast.makeText(SelectedPlaceActivity.this, placeSelectedID, Toast.LENGTH_SHORT).show();


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
        mImageView = (ImageView) findViewById(R.id.imageViewInSV1);
        mImageView2 = (ImageView) findViewById(R.id.imageViewInSV2);
       // imagesHSV = (LinearLayout) findViewById(R.id.imagesLinearLayout);
//        uri = (TextView) findViewById(R.id.uriTV);
        images = new ArrayList<>();
        images.add(R.drawable.atm);
        images.add(R.drawable.airport);
        images.add(R.drawable.aquarium);
        images.add(R.drawable.art_gallery);
        images.add(R.drawable.bank_dollar);






        try {
            JSONObject jObject = new JSONObject(placeSelectedID);
            JSONObject jObjectResult = (JSONObject) jObject.get("result");
            phoneTV.setText(jObjectResult.getString("formatted_phone_number"));
            addressTV.setText(jObjectResult.getString("formatted_address"));
            placeNameTV.setText(jObjectResult.getString("name"));
            myRB.setRating((float) jObjectResult.getDouble("rating"));
            myRatingBarTV.setText(myRB.getRating()+"");
            JSONArray reviews = (jObjectResult.getJSONArray("reviews"));
            Log.e("REVIEWS" ,"" + reviews.length());
            reviewsTV.setText("" + reviews.length() + " reviews");
            //  Log.e("sadasd","dsaasddasasd");

            //TODO da pitam krasi kak da obsluja JSON
            uri = jObjectResult.getString("website");
            if(uri.isEmpty()){
                Toast.makeText(SelectedPlaceActivity.this, "this palce dosnt have website", Toast.LENGTH_SHORT).show();
            }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = getImageBitmap("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4\n");
//                        mImageView.setImageBitmap(bitmap);
//                        Toast.makeText(SelectedPlaceActivity.this, mImageView.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            // Log.e("URI",uri);
            cashRB.setRating((int) jObjectResult.getDouble("price_level"));
            Log.e("PRICE_LEVEL",jObjectResult.getInt("price_level") + "");



        }
        catch (JSONException e1) {
            //  Log.e("URI EXCEPTION",uri);
            //cashRB.setRating(4);
            e1.printStackTrace();
        }







        // cashRB.setRating(2);
       // myRB.setRating(2.8f);
       // myRatingBarTV.setText(myRB.getRating() + "");

        //getting from json and put


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectedPlaceActivity.this, "website clicked", Toast.LENGTH_SHORT).show();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneTV.getText().toString()));
                startActivity(callIntent);
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

        hsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {




                hsv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                hsv.getHeight(); //height is ready
                // Toast.makeText(SelectedPlaceActivity.this, hsv.getHeight() + "", Toast.LENGTH_SHORT).show();
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                //  Toast.makeText(SelectedPlaceActivity.this, hsv.getChildAt(0).getHeight() + "", Toast.LENGTH_SHORT).show();
                mImageView.setLayoutParams(new LinearLayout.LayoutParams(width,hsv.getHeight()));
                mImageView2.setLayoutParams(new LinearLayout.LayoutParams(width,hsv.getHeight()));

                final ObjectAnimator animator=ObjectAnimator.ofInt(hsv, "scrollX",width);
                animator.setDuration(15000);
                animator.setRepeatCount(ObjectAnimator.INFINITE);


               animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//
                        Log.e("NEDKO","GENIQ");

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                        mImageView.setBackgroundResource(images.get(counterImages));
                        counterImages++;
                        mImageView2.setBackgroundResource(images.get(counterImages));
                        counterImages++;

                        Log.e("NEDKO","GENIdsasdadasdas");
                        if(counterImages >= images.size()-1){
                            counterImages=0;
                        }
                        hsv.scrollTo(0,0);
                    }
                });
                animator.start();
                //animator.setRepeatCount(ObjectAnimator.INFINITE);
                //animator.start();

            }
        });

//        hsv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hsv.smoothScrollTo(500, 0);
//            }
//        }, 1000);



//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                int x = 1;
//                while (true){
//                    if (!hsv.canScrollHorizontally(x)){
//                        x = -x;
//                    }
//                    final int finalX = x;
//                    hsv.scrollBy(finalX, 0);
//
//                    try {
//                        Thread.sleep(25);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();



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



        }

    }
    ////////////////////////////////////////////////////////////////////

    private static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Problem", "Error getting bitmap", e);
        }
        return bm;
    }

//    @Override
//    public void onClick(View view) {
//        new FetchBitmapTask().execute(ADDRESS);
//    }

    private class FetchBitmapTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                }
            });
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getImageBitmap(strings[0]);
        }
    }
}///////////////////////////



