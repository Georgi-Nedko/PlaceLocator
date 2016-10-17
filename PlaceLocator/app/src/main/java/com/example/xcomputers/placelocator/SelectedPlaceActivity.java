
package com.example.xcomputers.placelocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.xcomputers.placelocator.model.Commentator;
import com.example.xcomputers.placelocator.model.WifiManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

public class SelectedPlaceActivity extends AppCompatActivity {

    private Button call;
    private Button website;
    private Button map;
    private RatingBar myRB;
    private TextView myRatingBarTV;
    private TextView addressTV;
    private TextView openTimeTV;
    private TextView distanceTV;
    private String phoneTV;
    private TextView nameScrollTextView;
    private Location lastLocation;
    private Location placeLocation;
    private RecyclerView commentsRecyclerView;
    private String uri;
    private LinkedList<Bitmap> images;
    private int counterChild = 0;
    private ViewFlipper myViewFlipper;
    private Bitmap loadingImageBM;
    private Bitmap noImageBM;
    private ImageView loadingImage;
    private ArrayList<Commentator> commentators;
    private int displayHeight;
    private int displayWidth;
    private AlertDialog alertDialog;
    private WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place);
        wifiManager = WifiManager.getInstance(this);
        if (!wifiManager.isConnectingToInternet()) {
            alertDialog = wifiManager.promptUserToTurnOnWifi();
        }

        loadingImageBM = BitmapFactory.decodeResource(SelectedPlaceActivity.this.getResources(), R.drawable.image_coming_soon);
        noImageBM = BitmapFactory.decodeResource(SelectedPlaceActivity.this.getResources(), R.drawable.no_photo_available);

        String placeSelectedJSON = getIntent().getStringExtra("json");
        lastLocation = getIntent().getParcelableExtra("lastLocation");
        placeLocation = getIntent().getParcelableExtra("placeLocation");

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
        myViewFlipper = (ViewFlipper) findViewById(R.id.details_photo_scroll_view);

        commentsRecyclerView = (RecyclerView) findViewById(R.id.comments_RV);
        commentsRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.WHITE).build());
        commentators = new ArrayList<>();

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(SelectedPlaceActivity.this));
        SelectedPlaceRecyclerViewAdapter adapter = new SelectedPlaceRecyclerViewAdapter(SelectedPlaceActivity.this, commentators);
        commentsRecyclerView.setAdapter(adapter);

        nameScrollTextView.setSelected(true);
        addressTV.setSelected(true);
        openTimeTV.setSelected(true);

        Animation animationFlipIn = AnimationUtils.loadAnimation(SelectedPlaceActivity.this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(SelectedPlaceActivity.this, R.anim.flipout);
        myViewFlipper.setInAnimation(animationFlipIn);
        myViewFlipper.setOutAnimation(animationFlipOut);
        myViewFlipper.setFlipInterval(2400);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayHeight = displaymetrics.heightPixels;
        displayWidth = displaymetrics.widthPixels;
        myViewFlipper.getLayoutParams().height = (int) (displayHeight / 2.4);
        myViewFlipper.getLayoutParams().width = displayWidth;

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
            } else {
                openTimeTV.setText(jObjectResult.getJSONObject("opening_hours").getString("weekday_text"));
            }
            if (jObjectResult.isNull("rating")) {
                myRB.setRating(0);
                myRatingBarTV.setText(myRB.getRating() + "");
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
                    new ImageDownloaderTask().execute(("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + photos.getJSONObject(i).getString("photo_reference") + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4\n"));
                }
            } else {
                images.add(noImageBM);
                ImageView noImage = new ImageView(SelectedPlaceActivity.this);
                noImage.setImageBitmap(noImageBM);
                noImage.setScaleType(ImageView.ScaleType.FIT_XY);
                myViewFlipper.addView(noImage);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        distanceTV.setText(getIntent().getStringExtra("distance") + " , " + getIntent().getStringExtra("duration"));

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneTV.equals("no phone")) {
                    Toast.makeText(SelectedPlaceActivity.this, "This place does not have a phone number", Toast.LENGTH_SHORT).show();
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
                if (wifiManager.isConnectingToInternet()) {
                    if (uri == null) {
                        Toast.makeText(SelectedPlaceActivity.this, "This place does not have a website!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                        websiteIntent.setData(Uri.parse(uri));
                        startActivity(websiteIntent);
                    }
                } else {
                    alertDialog = wifiManager.promptUserToTurnOnWifi();
                }
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isConnectingToInternet()) {
                    Intent intent = new Intent(SelectedPlaceActivity.this, SelectedPlaceMapsActivity.class);
                    intent.putExtra("placeLocation", placeLocation);
                    intent.putExtra("name", nameScrollTextView.getText().toString());
                    startActivity(intent);
                } else {
                    alertDialog = wifiManager.promptUserToTurnOnWifi();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        if (!wifiManager.isConnectingToInternet()) {
            alertDialog = wifiManager.promptUserToTurnOnWifi();
        }
        super.onStart();
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            if (images.size() == 0) {
                images.add(loadingImageBM);
                loadingImage = new ImageView(SelectedPlaceActivity.this);
                loadingImage.setImageBitmap(loadingImageBM);
                loadingImage.setScaleType(ImageView.ScaleType.FIT_XY);
                myViewFlipper.addView(loadingImage);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }
            if (images.contains(loadingImageBM)) {
                images.removeFirst();
                myViewFlipper.removeView(loadingImage);
            }
            images.add(bitmap);
            ImageView newImageView = new ImageView(SelectedPlaceActivity.this);
            myViewFlipper.addView(newImageView);

            ((ImageView) myViewFlipper.getChildAt(counterChild)).setImageBitmap(bitmap);
            ((ImageView) myViewFlipper.getChildAt(counterChild)).setScaleType(ImageView.ScaleType.FIT_XY);
            counterChild++;
            myViewFlipper.startFlipping();
            if (images.size() <= 1) {
                myViewFlipper.stopFlipping();
            }
        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
        } finally {
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