
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
import android.widget.ImageView;
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


        String placeSelectedJSON = getIntent().getStringExtra("json");
        Log.e("JSON", placeSelectedJSON);
        lastLocation = getIntent().getParcelableExtra("lastLocation");
        Log.e("LAST LOCATION", String.valueOf(lastLocation.getLatitude()) + "," + String.valueOf(lastLocation.getLongitude()));
        placeLocation = getIntent().getParcelableExtra("placeLocation");
        Log.e("PlACE LOCATION", String.valueOf(placeLocation.getLatitude() + "," + String.valueOf(placeLocation.getLongitude())));


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
                    Log.e("PHOTOSSSS", photos.toString());
                    Log.e("photoreference", "" + photos.getJSONObject(i).getString("photo_reference"));
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
                if (uri == null) {
                    Toast.makeText(SelectedPlaceActivity.this, "This place does not have a website!", Toast.LENGTH_SHORT).show();
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
            Log.e("IMAGESSVALQMSNIMKA", params[0]);

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
            Log.e("IMAGESADD", bitmap.toString());
            Log.e("IMAGES1", images.size() + "");
            Log.e("IMAGES", images.size() + "");



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