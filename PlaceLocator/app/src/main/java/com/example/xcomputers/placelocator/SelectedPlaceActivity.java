
package com.example.xcomputers.placelocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import com.example.xcomputers.placelocator.model.Commentator;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import io.techery.properratingbar.ProperRatingBar;

public class SelectedPlaceActivity extends AppCompatActivity {
    private HorizontalScrollView hsv;
    private Button call;
    private Button website;
    private Button map;
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
    private RecyclerView commentsRecyclerView;

    String uri;
    ImageView mImageView;
    ImageView mImageView2;
    HorizontalScrollView imagesHSV;
    LinkedList<Bitmap> images;
    int counterImages = 0;
    int counterChild = 0;
    int counterEven = 3;
    int counterOdd = 4;
    private ViewFlipper MyViewFlipper;
    Bitmap loadingImageBM;
    Bitmap noImageBM;
    ImageView loadingImage;
    private ArrayList<Commentator> commentators;
    int height;
    int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
        setContentView(R.layout.activity_selected_place);

        loadingImageBM = BitmapFactory.decodeResource(SelectedPlaceActivity.this.getResources(), R.drawable.image_coming_soon);
        noImageBM = BitmapFactory.decodeResource(SelectedPlaceActivity.this.getResources(), R.drawable.no_photo_available);
        //TODO make xml in scroll view
        //TODO change the horizontal scroll images
        //TODO make the recylcer view for comments

        //TODO change the placeid with placeSelectedID


        //Toast.makeText(SelectedPlaceActivity.this, placeInfo, Toast.LENGTH_SHORT).show();
        String placeSelectedJSON = getIntent().getStringExtra("json");
        Log.e("JSON", placeSelectedJSON);
        lastLocation = getIntent().getParcelableExtra("lastLocation");
        Log.e("LAST LOCATION", String.valueOf(lastLocation.getLatitude()) + "," + String.valueOf(lastLocation.getLongitude()));
        placeLocation = getIntent().getParcelableExtra("placeLocation");
        Log.e("PlACE LOCATION", String.valueOf(placeLocation.getLatitude() + "," + String.valueOf(placeLocation.getLongitude())));


        //  new ImageDownloaderTask(new ImageView(SelectedPlaceActivity.this)).execute(("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4\n"));


        call = (Button) findViewById(R.id.details_dial_button);
        map = (Button) findViewById(R.id.details_reserve_button);
        website = (Button) findViewById(R.id.details_website_url);
        myRB = (RatingBar) findViewById(R.id.details_rating_rb);
        myRatingBarTV = (TextView) findViewById(R.id.details_rating_tv);
        addressTV = (TextView) findViewById(R.id.details_address);
        nameScrollTextView = (TextView) findViewById(R.id.nameTextView);
        openTimeTV = (TextView) findViewById(R.id.details_open);
        distanceTV = (TextView) findViewById(R.id.details_ETA);
        images = new LinkedList<>();
        MyViewFlipper = (ViewFlipper) findViewById(R.id.details_photo_scroll_view);

        commentsRecyclerView = (RecyclerView) findViewById(R.id.comments_RV);
        commentsRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.WHITE).build());
        commentators = new ArrayList<>();
        //addAllCommentators();
        //addAllCategories(list);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(SelectedPlaceActivity.this));
        SelectedPlaceRecyclerViewAdapter adapter = new SelectedPlaceRecyclerViewAdapter(SelectedPlaceActivity.this, commentators);
        commentsRecyclerView.setAdapter(adapter);


        nameScrollTextView.setSelected(true);
        addressTV.setSelected(true);
        openTimeTV.setSelected(true);


        Animation animationFlipIn = AnimationUtils.loadAnimation(SelectedPlaceActivity.this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(SelectedPlaceActivity.this, R.anim.flipout);
        MyViewFlipper.setInAnimation(animationFlipIn);
        MyViewFlipper.setOutAnimation(animationFlipOut);
        MyViewFlipper.setFlipInterval(2400);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        MyViewFlipper.getLayoutParams().height = (int) (height / 2.4);
        MyViewFlipper.getLayoutParams().width = width;


        try {
            JSONObject jObject = new JSONObject(placeSelectedJSON);
            JSONObject jObjectResult = (JSONObject) jObject.get("result");
            addressTV.setText(jObjectResult.getString("formatted_address"));
            nameScrollTextView.setText(jObjectResult.getString("name"));
            if (!jObjectResult.isNull("reviews")) {
                addAllCommentators(jObjectResult, commentators);
            }

            if (jObjectResult.isNull("formatted_phone_number")) {
                phoneTV = "no phone";
            } else {
                phoneTV = (jObjectResult.getString("formatted_phone_number"));
            }
            if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
                openTimeTV.setText("Not given open time!");
                Toast.makeText(SelectedPlaceActivity.this, "open time  are null", Toast.LENGTH_SHORT).show();
            } else {
                openTimeTV.setText(jObjectResult.getJSONObject("opening_hours").getString("weekday_text"));
            }
            if (jObjectResult.isNull("rating")) {
                Toast.makeText(SelectedPlaceActivity.this, "rating is null", Toast.LENGTH_SHORT).show();
                myRB.setRating(0);
                myRatingBarTV.setText(myRB.getRating() + "");
//                 if (!jObjectResult.isNull("website")) {
//                     uri = jObjectResult.getString("website");
//                 }
            } else {
                myRB.setRating((float) jObjectResult.getDouble("rating"));
                myRatingBarTV.setText(myRB.getRating() + "");
            }
            if (!jObjectResult.isNull("website")) {
                uri = jObjectResult.getString("website");
            }
            if (!jObjectResult.isNull("photos")) {
                JSONArray photos = (JSONArray) jObjectResult.get("photos");
                for (int i = 0; i < photos.length(); i++) {
                    Log.e("PHOTOSSSS", photos.toString());
                    Log.e("photoreference", "" + photos.getJSONObject(i).getString("photo_reference"));
                    new ImageDownloaderTask().execute(("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + photos.getJSONObject(i).getString("photo_reference") + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4\n"));

                }
            } else {
                images.add(noImageBM);
                ImageView noImage = new ImageView(SelectedPlaceActivity.this);
                noImage.setImageBitmap(noImageBM);
                noImage.setScaleType(ImageView.ScaleType.FIT_XY);
                MyViewFlipper.addView(noImage);
            }
//        }
//                        }
//
//                    } else {
//                        phoneTV = (jObjectResult.getString("formatted_phone_number"));
//                        if (jObjectResult.isNull("formatted_address")) {
//                            Toast.makeText(SelectedPlaceActivity.this, "address is null", Toast.LENGTH_SHORT).show();
//                            addressTV.setText("dsadsaasd");
//                        } else {
//                            addressTV.setText(jObjectResult.getString("formatted_address"));
//                            nameScrollTextView.setText(jObjectResult.getString("name"));
//                            if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
//                                Toast.makeText(SelectedPlaceActivity.this, "open time is null", Toast.LENGTH_SHORT).show();
//                                openTimeTV.setText("Not given open time!");
//                            } else {
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
//                }
//                else{
//                    images.add(noImageBM);
//                    ImageView noImage = new ImageView(SelectedPlaceActivity.this);
//                    noImage.setImageBitmap(noImageBM);
//                    noImage.setScaleType(ImageView.ScaleType.FIT_XY);
//                    //newImageView.getLayoutParams().height = 20;
//                    //newImageView.getLayoutParams().width = MyViewFlipper.getWidth();
//                    MyViewFlipper.addView(noImage);
//
//                    if (jObjectResult.isNull("formatted_phone_number")) {
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
//                    } else {
//                        phoneTV = (jObjectResult.getString("formatted_phone_number"));
//                        if (jObjectResult.isNull("formatted_address")) {
//                            Toast.makeText(SelectedPlaceActivity.this, "address is null", Toast.LENGTH_SHORT).show();
//                            addressTV.setText("dsadsaasd");
//                        } else {
//                            addressTV.setText(jObjectResult.getString("formatted_address"));
//                            nameScrollTextView.setText(jObjectResult.getString("name"));
//                            if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
//                                Toast.makeText(SelectedPlaceActivity.this, "open time is null", Toast.LENGTH_SHORT).show();
//                                openTimeTV.setText("Not given open time!");
//                            } else {
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
//                }
//
//            }
//            //////////////////////////////////////////////////////////////////////////////////////////////
//            else{
//
//                if(!jObjectResult.isNull("photos")) {
//                    JSONArray photos = (JSONArray) jObjectResult.get("photos");
//                    for(int i = 0; i < photos.length();i++){
//                        Log.e("PHOTOSSSS", photos.toString());
//                        Log.e("photoreference","" + photos.getJSONObject(i).getString("photo_reference"));
//                        new ImageDownloaderTask(new ImageView(SelectedPlaceActivity.this)).execute(("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference="+ photos.getJSONObject(i).getString("photo_reference") + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4\n"));
//                    }
//
//                    if (jObjectResult.isNull("formatted_phone_number")) {
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
//                    } else {
//                        phoneTV = (jObjectResult.getString("formatted_phone_number"));
//                        if (jObjectResult.isNull("formatted_address")) {
//                            Toast.makeText(SelectedPlaceActivity.this, "address is null", Toast.LENGTH_SHORT).show();
//                            addressTV.setText("dsadsaasd");
//                        } else {
//                            addressTV.setText(jObjectResult.getString("formatted_address"));
//                            nameScrollTextView.setText(jObjectResult.getString("name"));
//                            if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
//                                Toast.makeText(SelectedPlaceActivity.this, "open time is null", Toast.LENGTH_SHORT).show();
//                                openTimeTV.setText("Not given open time!");
//                            } else {
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
//                }
//                else{
//                    images.add(noImageBM);
//                    ImageView noImage = new ImageView(SelectedPlaceActivity.this);
//                    noImage.setImageBitmap(noImageBM);
//                    noImage.setScaleType(ImageView.ScaleType.FIT_XY);
//                    MyViewFlipper.addView(noImage);
//
//                    if (jObjectResult.isNull("formatted_phone_number")) {
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
//                    } else {
//                        phoneTV = (jObjectResult.getString("formatted_phone_number"));
//                        if (jObjectResult.isNull("formatted_address")) {
//                            Toast.makeText(SelectedPlaceActivity.this, "address is null", Toast.LENGTH_SHORT).show();
//                            addressTV.setText("dsadsaasd");
//                        } else {
//                            addressTV.setText(jObjectResult.getString("formatted_address"));
//                            nameScrollTextView.setText(jObjectResult.getString("name"));
//                            if (jObjectResult.isNull("opening_hours") || jObjectResult.getJSONObject("opening_hours").isNull("weekday_text")) {
//                                Toast.makeText(SelectedPlaceActivity.this, "open time is null", Toast.LENGTH_SHORT).show();
//                                openTimeTV.setText("Not given open time!");
//                            } else {
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
//                }
//
//            }


        } catch (JSONException e1) {

            e1.printStackTrace();
        }
        distanceTV.setText(getIntent().getStringExtra("distance") + " , " + getIntent().getStringExtra("duration"));


        //getting from json and put


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneTV.equals("no phone")) {
                    Toast.makeText(SelectedPlaceActivity.this, "This place does not have number", Toast.LENGTH_SHORT).show();
                } else {
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
                if (uri == null) {
                    Toast.makeText(SelectedPlaceActivity.this, "This place does not have a website", Toast.LENGTH_SHORT).show();
                } else {

                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW);


                    websiteIntent.setData(Uri.parse(uri));


                    startActivity(websiteIntent);
                }
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedPlaceActivity.this, SelectedPlaceMapsActivity.class);
                intent.putExtra("placeLocation", placeLocation);
                intent.putExtra("name", nameScrollTextView.getText().toString());
                startActivity(intent);
            }
        });

    }


    ////////////////////////////////////////////////////////////////////

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            if (images.size() == 0) {
                images.add(loadingImageBM);
                loadingImage = new ImageView(SelectedPlaceActivity.this);
                loadingImage.setImageBitmap(loadingImageBM);
                loadingImage.setScaleType(ImageView.ScaleType.FIT_XY);
                MyViewFlipper.addView(loadingImage);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.e("IMAGESSVALQMSNIMKA", params[0]);

            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }

//             if (imageViewReference != null) {
//                 ImageView imageView = imageViewReference.get();
//                 if (imageView != null) {
//                        if (bitmap != null) {
            images.add(bitmap);
            ImageView newImageView = new ImageView(SelectedPlaceActivity.this);
            MyViewFlipper.addView(newImageView);
            Log.e("IMAGESADD", bitmap.toString());
            Log.e("IMAGES1", images.size() + "");


            Log.e("IMAGES", images.size() + "");

//            for (int i = 0; i < images.size(); i++) {
//                Log.e("IMAGESCOUNTTT", "" + MyViewFlipper.getChildCount());
//                // ImageView newImageView = );
//                ((ImageView) MyViewFlipper.getChildAt(i)).setImageBitmap(images.get(i));
//                ((ImageView) MyViewFlipper.getChildAt(i)).setScaleType(ImageView.ScaleType.FIT_XY);
//                //MyViewFlipper.addView(newImageView);
//            }
            if (images.contains(loadingImageBM)) {
                images.removeFirst();
                MyViewFlipper.removeView(loadingImage);
            }
            ((ImageView) MyViewFlipper.getChildAt(counterChild)).setImageBitmap(bitmap);
            ((ImageView) MyViewFlipper.getChildAt(counterChild)).setScaleType(ImageView.ScaleType.FIT_XY);
             counterChild++;


            MyViewFlipper.startFlipping();
            if (images.size() <= 1) {
                MyViewFlipper.stopFlipping();
            }


        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            Log.e("IMAGESTATUS", statusCode + "");
            if (statusCode != 200) {
                Log.e("IMAGESTATUS!=200", statusCode + "");
                return null;
            }
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Log.e("IMAGESBITMAP", bitmap + "");
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("IMAGESDownloader", "Error downloading image from " + url);
        } finally {
            Log.e("IMAGESFINALY", urlConnection + "");
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }
        return null;
    }

    void addAllCommentators(JSONObject jsonObject, ArrayList<Commentator> commentators) throws JSONException {
        JSONArray reviews = jsonObject.getJSONArray("reviews");
        for (int i = 0; i < reviews.length(); i++) {
            String name = reviews.getJSONObject(i).getString("author_name");
            String description = reviews.getJSONObject(i).getString("text");
            float rating = (float) reviews.getJSONObject(i).getDouble("rating");
            commentators.add(new Commentator(name, description, rating));

        }
    }
}