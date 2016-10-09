
package com.example.xcomputers.placelocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
    private Button call;
    private Button website;
    private RatingBar myRB;
    private ProperRatingBar cashRB;
    private TextView myRatingBarTV;
    private TextView placeNameTV;
    private TextView reviewsTV;
    private TextView addressTV;
    private TextView openTimeTV;
    private TextView distanceTV;
    private String phoneTV;
    private TextView nameScrollTextView;
    private Location lastLocation;
    private Location placeLocation;
    private View categoriesRecyclerViewAdapter;
    //private String placeSelectedID;
    private static Float kilometers;
    private LinearLayout imagesLL;

    String uri;
    ImageView mImageView;
    ImageView mImageView2;
    HorizontalScrollView imagesHSV;
    ArrayList<Integer> images;
    int counterImages = 0;
    int counter=1;
    int counterEven = 3;
    int counterOdd = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place);
        //TODO make xml in scroll view
        //TODO change the horizontal scroll images
        //TODO make the recylcer view for comments

        //TODO change the placeid with placeSelectedID

        final ViewFlipper MyViewFlipper = (ViewFlipper)findViewById(R.id.details_photo_scroll_view);

       // final Button buttonAutoFlip = (Button)findViewById(R.id.buttonautoflip);
        //Button button1 = (Button)findViewById(R.id.button1);
        //Button button2 = (Button)findViewById(R.id.button2);
        final ArrayList<Integer> images;
        Animation animationFlipIn  = AnimationUtils.loadAnimation(this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(this, R.anim.flipout);
        MyViewFlipper.setInAnimation(animationFlipIn);
        MyViewFlipper.setOutAnimation(animationFlipOut);
        MyViewFlipper.setFlipInterval(1500);

        images = new ArrayList<>();
        images.add(R.drawable.airport);
        images.add(R.drawable.aquarium);
        images.add(R.drawable.art_gallery);
        images.add(R.drawable.amusement);

        for(int i =0;i < images.size();i++){
            ImageView newImageView = new ImageView(SelectedPlaceActivity.this);
            newImageView.setBackgroundResource(images.get(i));
            MyViewFlipper.addView(newImageView);
        }
        MyViewFlipper.startFlipping();







//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                MyViewFlipper.showNext();
//            }});
//
//
//        button2.setOnClickListener(new Button.OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                MyViewFlipper.showNext();
//
//               // MyViewFlipper.showPrevious();
//            }});
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyViewFlipper.showNext();
//            }
//        });






        //Toast.makeText(SelectedPlaceActivity.this, placeInfo, Toast.LENGTH_SHORT).show();
        String placeSelectedJSON = getIntent().getStringExtra("json");
        Log.e("JSON", placeSelectedJSON);
        lastLocation = getIntent().getParcelableExtra("lastLocation");
        Log.e("LAST LOCATION", String.valueOf(lastLocation.getLatitude())+ "," +String.valueOf(lastLocation.getLongitude()));
        placeLocation = getIntent().getParcelableExtra("placeLocation");
        Log.e("PlACE LOCATION", String.valueOf(placeLocation.getLatitude()+ ","+String.valueOf(placeLocation.getLongitude())));
        // kilometers = (lastLocation.distanceTo(placeLocation))/1000;
        // Toast.makeText(SelectedPlaceActivity.this, placeSelectedID, Toast.LENGTH_SHORT).show();

        //TODO REQUUEST_DENIED
        //new RequestTask().execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");
        //new RequestTask().execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+ String.valueOf(lastLocation.getLatitude())+ ","+String.valueOf(lastLocation.getLongitude() + "&destinations="+ String.valueOf(placeLocation.getLatitude())+ "," +String.valueOf(placeLocation.getLongitude() +"&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4")));
        // Log.e("mytask", mytask.getStatus().toString());



      //  hsv = (HorizontalScrollView) findViewById(R.id.details_photo_scroll_view);
        call = (Button) findViewById(R.id.details_dial_button);
        website = (Button) findViewById(R.id.details_website_url);
        myRB = (RatingBar) findViewById(R.id.details_rating_rb);
        myRatingBarTV = (TextView) findViewById(R.id.details_rating_tv);
        //placeNameTV = (TextView) findViewById(R.id.nameTextView);
        // reviewsTV = (TextView) findViewById(R.id.reviewsTV);
        addressTV = (TextView) findViewById(R.id.details_address);
        nameScrollTextView = (TextView) findViewById(R.id.nameTextView);
        openTimeTV = (TextView) findViewById(R.id.details_open);
        distanceTV = (TextView) findViewById(R.id.details_ETA);
       // imagesLL = (LinearLayout) findViewById(R.id.details_photo_holder);
        //  distanceTV.setText(""+ kilometers + "KM");


        // phoneTV = (TextView) findViewById(R.id.det);
        //cashRB = (ProperRatingBar) findViewById(R.id.MyCashRatingBar);
        // categoriesRecyclerViewAdapter = findViewById(R.id.recycler_view_selected_place);
      //  mImageView = (ImageView) findViewById(R.id.image1);
        //  mImageView2 = (ImageView) findViewById(R.id.image2);
        // imagesHSV = (LinearLayout) findViewById(R.id.imagesLinearLayout);
//        uri = (TextView) findViewById(R.id.uriTV);
//        images = new ArrayList<>();
//        images.add(R.drawable.atm);
//        images.add(R.drawable.airport);
//        images.add(R.drawable.aquarium);
//        images.add(R.drawable.art_gallery);
//        images.add(R.drawable.bank_dollar);
        //addressTV.setMovementMethod(new ScrollingMovementMethod());

        nameScrollTextView.setSelected(true);
        addressTV.setSelected(true);
        openTimeTV.setSelected(true);


        // new RequestTask().execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY");


        try {
            JSONObject jObject = new JSONObject(placeSelectedJSON);
            JSONObject jObjectResult = (JSONObject) jObject.get("result");

            if (jObjectResult.isNull("formatted_phone_number")) {
                phoneTV = "no phone";
                if (jObjectResult.isNull("formatted_address")) {
                    addressTV.setText("dsadsaasd");
                } else {
                    addressTV.setText(jObjectResult.getString("formatted_address"));
                    nameScrollTextView.setText(jObjectResult.getString("name"));
                    if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
                        openTimeTV.setText("Not given open time!");
                        Toast.makeText(SelectedPlaceActivity.this, "open time  are null", Toast.LENGTH_SHORT).show();
                    } else {
                        openTimeTV.setText(jObjectResult.getJSONObject("opening_hours").getString("weekday_text"));

                        if (jObjectResult.isNull("rating")) {
                            Toast.makeText(SelectedPlaceActivity.this, "rating is null", Toast.LENGTH_SHORT).show();
                            myRB.setRating(0);
                            myRatingBarTV.setText(myRB.getRating() + "");
                            if (!jObjectResult.isNull("website")) {
                                uri = jObjectResult.getString("website");
                            }
                        } else {
                            myRB.setRating((float) jObjectResult.getDouble("rating"));
                            myRatingBarTV.setText(myRB.getRating() + "");
                            if (jObjectResult.isNull("website")) {
                                uri = jObjectResult.getString("website");
                            }
                        }
                    }
                }

            } else {
                phoneTV = (jObjectResult.getString("formatted_phone_number"));
                if (jObjectResult.isNull("formatted_address")) {
                    Toast.makeText(SelectedPlaceActivity.this, "address is null", Toast.LENGTH_SHORT).show();
                    addressTV.setText("dsadsaasd");
                } else {
                    addressTV.setText(jObjectResult.getString("formatted_address"));
                    nameScrollTextView.setText(jObjectResult.getString("name"));
                    if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
                        Toast.makeText(SelectedPlaceActivity.this, "open time is null", Toast.LENGTH_SHORT).show();
                        openTimeTV.setText("Not given open time!");
                    } else {
                        openTimeTV.setText(jObjectResult.getJSONObject("opening_hours").getString("weekday_text"));
                    }
                    if (jObjectResult.isNull("rating")) {
                        Toast.makeText(SelectedPlaceActivity.this, "rating is null", Toast.LENGTH_SHORT).show();
                        myRB.setRating(0);
                        myRatingBarTV.setText(myRB.getRating() + "");
                        if (!jObjectResult.isNull("website")) {
                            uri = jObjectResult.getString("website");
                        }
                    } else {
                        myRB.setRating((float) jObjectResult.getDouble("rating"));
                        myRatingBarTV.setText(myRB.getRating() + "");
                        if (!jObjectResult.isNull("website")) {
                            uri = jObjectResult.getString("website");
                        }
                    }


                }
            }
//        }
//            else{
//               // reviewsTV.setText("" + reviews.length() + " reviews");
//                    if(jObjectResult.isNull("formatted_phone_number")){
//                        phoneTV = "no phone";
//                        if (jObjectResult.isNull("formatted_address")) {
//                            addressTV.setText("dsadsaasd");
//                        } else {
//                            addressTV.setText(jObjectResult.getString("formatted_address"));
//                            nameScrollTextView.setText(jObjectResult.getString("name"));
//                            if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
//                                openTimeTV.setText("Not given open time!");
//                                Toast.makeText(SelectedPlaceActivity.this, "open time  are null", Toast.LENGTH_SHORT).show();
//                            } else {
//                                openTimeTV.setText(jObjectResult.getJSONObject("opening_hours").getString("weekday_text"));
//
//                                if (jObjectResult.isNull("rating")) {
//                                    Toast.makeText(SelectedPlaceActivity.this, "rating is null", Toast.LENGTH_SHORT).show();
//                                    myRB.setRating(0);
//                                    myRatingBarTV.setText(myRB.getRating() + "");
//                                    if (!jObjectResult.isNull("website")) {
//                                        uri = jObjectResult.getString("website");
//                                    }
//                                } else {
//                                    myRB.setRating((float) jObjectResult.getDouble("rating"));
//                                    myRatingBarTV.setText(myRB.getRating() + "");
//                                    if (jObjectResult.isNull("website")) {
//                                        uri = jObjectResult.getString("website");
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                    else {
//                        phoneTV = (jObjectResult.getString("formatted_phone_number"));
//                        if (jObjectResult.isNull("formatted_address")) {
//                            Toast.makeText(SelectedPlaceActivity.this, "address is null", Toast.LENGTH_SHORT).show();
//                            addressTV.setText("dsadsaasd");
//                        } else {
//                            addressTV.setText(jObjectResult.getString("formatted_address"));
//                            nameScrollTextView.setText(jObjectResult.getString("name"));
//                            if(jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")){
//                                Toast.makeText(SelectedPlaceActivity.this, "open time is null", Toast.LENGTH_SHORT).show();
//                                openTimeTV.setText("Not given open time!");
//                            }else {
//                                openTimeTV.setText(jObjectResult.getJSONObject("opening_hours").getString("weekday_text"));
//                            }
//                            if (jObjectResult.isNull("rating")) {
//                                Toast.makeText(SelectedPlaceActivity.this, "rating is null", Toast.LENGTH_SHORT).show();
//                                myRB.setRating(0);
//                                myRatingBarTV.setText(myRB.getRating() + "");
//                                if (!jObjectResult.isNull("website")) {
//                                    uri = jObjectResult.getString("website");
//                                }
//                            } else {
//                                myRB.setRating((float) jObjectResult.getDouble("rating"));
//                                myRatingBarTV.setText(myRB.getRating() + "");
//                                if (!jObjectResult.isNull("website")) {
//                                    uri = jObjectResult.getString("website");
//                                }
//                            }
//
//
//                        }
//                    }





//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = getImageBitmap("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4\n");
//                        mImageView.setImageBitmap(bitmap);
//                        Toast.makeText(SelectedPlaceActivity.this, mImageView.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            // Log.e("URI",uri);
            // cashRB.setRating((int) jObjectResult.getDouble("price_level"));
            //  Log.e("PRICE_LEVEL",jObjectResult.getInt("price_level") + "");




        }
        catch (JSONException e1) {
            //  Log.e("URI EXCEPTION",uri);
            //cashRB.setRating(4);
            e1.printStackTrace();
        }
        distanceTV.setText(getIntent().getStringExtra("distance") +  " , "+ getIntent().getStringExtra("duration"));







        //getting from json and put


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneTV.equals("no phone")){
                    Toast.makeText(SelectedPlaceActivity.this, "This place does not have number", Toast.LENGTH_SHORT).show();
                }else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phoneTV));
                    startActivity(callIntent);
                }
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SelectedPlaceActivity.this, "website clicked", Toast.LENGTH_SHORT).show();
                //Log.e("URI",uri);
                if(uri == null) {
                    Toast.makeText(SelectedPlaceActivity.this, "This place does not have a website", Toast.LENGTH_SHORT).show();
                }else {

                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW);


                    websiteIntent.setData(Uri.parse(uri));


                    startActivity(websiteIntent);
                }
            }
        });

//        hsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//
//                hsv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                hsv.getHeight(); //height is ready
//                // Toast.makeText(SelectedPlaceActivity.this, hsv.getHeight() + "", Toast.LENGTH_SHORT).show();
//                Display display = getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//                final int width = size.x;
//                int height = size.y;
//
//                //  Toast.makeText(SelectedPlaceActivity.this, hsv.getChildAt(0).getHeight() + "", Toast.LENGTH_SHORT).show();
//                mImageView.setLayoutParams(new LinearLayout.LayoutParams(width,hsv.getHeight()));
//                mImageView2.setLayoutParams(new LinearLayout.LayoutParams(width,hsv.getHeight()));
//
//                final ObjectAnimator animator=ObjectAnimator.ofInt(hsv, "scrollX",width);
//                animator.setDuration(6000);
//                //animator.setRepeatCount(ObjectAnimator.INFINITE);
//
//                Log.e("WIDTH",width + "");
//
//
//                animator.addListener(new Animator.AnimatorListener() {
//                     int maxWidth = counter*width;
//                    @Override
//                    public void onAnimationStart(Animator animation) {
////                        hsv.scrollTo(maxWidth,0);
////                        counter+=1;
////
////                        Log.e("NEDKO","GENIdsasdadasdas");
////                        if(maxWidth == width*counterEven){
////                            counterEven+=2;
////                            ImageView newImg = new ImageView(SelectedPlaceActivity.this);
////                            newImg.setBackgroundResource(images.get(counterImages));
////                            imagesLL.addView(newImg,maxWidth,hsv.getHeight());
////
////
////                            counterImages++;
////                        }
////                        if(maxWidth == width*counterOdd){
////                            counterOdd+=2;
////                            ImageView newImg = new ImageView(SelectedPlaceActivity.this);
////                            newImg.setBackgroundResource(images.get(counterImages));
////                            imagesLL.addView(newImg,maxWidth,hsv.getHeight());
//////                            mImageView.setBackgroundResource(images.get(counterImages));
////                            counterImages++;
////                        }
////                        maxWidth = counter*width;
////                        if(counterImages >= images.size()-1){
////                            counterImages=0;
////                        }
////                        Log.e("COUNTER", counter + "");
////                        Log.e("COUNTEREVEN", counterEven + "");
////                        Log.e("COUNTERODD", counterOdd + "");
////                        Log.e("COUNTERMAXWIDTH", maxWidth + "");
////                        Log.e("NEDKO","GENIQ");
////
////                       // animator.start();
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
////
////                         hsv.scrollTo(maxWidth,0);
////                        counter+=2;
////
////                        Log.e("NEDKO","GENIdsasdadasdas");
////                        if(maxWidth == width*counterEven){
////                            counterEven+=2;
////                            ImageView newImg = new ImageView(SelectedPlaceActivity.this);
////                            newImg.setBackgroundResource(images.get(counterImages));
////                            imagesLL.addView(newImg,maxWidth,hsv.getHeight());
////
////
////                            counterImages++;
////                        }
////                        if(maxWidth == width*counterOdd){
////                            counterOdd+=2;
////                            ImageView newImg = new ImageView(SelectedPlaceActivity.this);
////                            newImg.setBackgroundResource(images.get(counterImages));
////                            imagesLL.addView(newImg,maxWidth,hsv.getHeight());
//////                            mImageView.setBackgroundResource(images.get(counterImages));
//////                            counterImages++;
////                        }
////                        maxWidth = counter*width;
////                        if(counterImages >= images.size()-1){
////                            counterImages=0;
////                        }
////                        Log.e("COUNTER", counter + "");
////                        Log.e("COUNTEREVEN", counterEven + "");
////                        Log.e("COUNTERODD", counterOdd + "");
////                        Log.e("COUNTERMAXWIDTH", maxWidth + "");
////                        Log.e("NEDKO","GENIQ");
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
////                         mImageView.setBackgroundResource(images.get(counterImages));
////                         counterImages++;
//
//
//                    }
//                });
//                animator.start();
//                //animator.setRepeatCount(ObjectAnimator.INFINITE);
//                //animator.start();
//
//            }
//        });

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
                Log.e("dddd","dddd");
                URL url = new URL(address);
                Log.e("dddd",url + "");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int status = connection.getResponseCode();
                Log.e("dddd",status + "");
                Scanner sc = new Scanner(connection.getInputStream());
                while (sc.hasNextLine()) {
                    Log.e("dddd1","dddd1");
                    response += sc.nextLine();
                }
            } catch (IOException e) {
                Log.e("dddd2","dddd2");
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Log.e("ZAQVKATA",s);
                JSONObject obj = new JSONObject(s);
                JSONArray arrayRows = obj.getJSONArray("rows");
                Log.e("ROWS",arrayRows.toString());
                JSONObject firstRowObject = arrayRows.getJSONObject(0);
                Log.e("tth",firstRowObject.toString());
                JSONArray elementsArray = firstRowObject.getJSONArray("elements");
                Log.e("elements",elementsArray.toString());
                String distanceInMiles = elementsArray.getJSONObject(0).getJSONObject("distance").getString("text").split(" ")[0];
                Log.e("distanceInMiles",distanceInMiles+"");
                double distanceInKM = ((Double.parseDouble(distanceInMiles))*1.609344);
                double distanceInKMToSecondSymbol = Math.floor(distanceInKM * 100) / 100;
                Log.e("distanceInKM",distanceInKM+"");
                // distanceTV.setText(obj.getJSONArray("rows").getJSONArray(0).getJSONObject(0).getString("distance"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


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
}