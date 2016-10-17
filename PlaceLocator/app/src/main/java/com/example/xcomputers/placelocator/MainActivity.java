package com.example.xcomputers.placelocator;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xcomputers.placelocator.model.Category;
import com.example.xcomputers.placelocator.model.WifiManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button voiceRecognitionButton;
    private String latitude;
    private String longtitude;
    private RecyclerView categoriesView;
    private TextView distanceTV;
    private TextView selectDistanceTV;
    private TextView distanceKMTV;
    private SeekBar distanceSeekBar;
    private ProgressDialog LocationDialog;
    private ProgressDialog requestLoadingDialog;
    private Location placeLocation;
    private String distance;
    private String duration;
    private String myRadiusString;
    private double myRadiusDouble;
    public static GoogleApiClient client;
    private Location lastLocation;
    private PlaceAutocompleteFragment autocompleteFragment;
    private static final int VOICE_RECOGNITION_REQUEST = 200;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Dialog alertDialog;
    private String autocompletePlaceID;
    private static final int PROGRESS_BAR_MULTIPLIER = 1000;
    private static final int LOCATION_REQUEST_INTERVAL = 1000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 1000;
    private boolean isLocationOn;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        wifiManager = WifiManager.getInstance(this);
        selectDistanceTV = (TextView) findViewById(R.id.distance_left_TV);
        distanceKMTV = (TextView) findViewById(R.id.distance_right_TV);
        selectDistanceTV.setTypeface(custom_font);
        distanceKMTV.setTypeface(custom_font);
        distanceSeekBar = (SeekBar) findViewById(R.id.distance_seek_bar);
        distanceSeekBar.setProgress(1);
        myRadiusString = Integer.toString(distanceSeekBar.getProgress() * PROGRESS_BAR_MULTIPLIER);
        distanceTV = (TextView) findViewById(R.id.distance_middle_TV);
        distanceTV.setText(distanceSeekBar.getProgress() + "");
        voiceRecognitionButton = (Button) findViewById(R.id.button);
        voiceRecognitionButton.setTypeface(custom_font);

        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(LocationServices.API)
                    .build();
        }
        requestLocation();

        voiceRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_recognition_dialog_text));
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, VOICE_RECOGNITION_REQUEST);
            }
        });


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(-1);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                autocompletePlaceID = place.getId();
                new RequestTask().execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + autocompletePlaceID + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");
            }

            @Override
            public void onError(Status status) {
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        categoriesView = (RecyclerView) findViewById(R.id.recycler_view);
        categoriesView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.WHITE).build());
        final List<Category> list = new ArrayList<>();
        addAllCategories(list);

        categoriesView.setLayoutManager(new LinearLayoutManager(this));
        CategoriesRecyclerViewAdapter adapter = new CategoriesRecyclerViewAdapter(this, list);
        categoriesView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CategoriesRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showLocationProgressDialog();
                requestLocation();
                getLocation();
                if (wifiManager.isConnectingToInternet()) {
                    if (lastLocation != null) {
                        executeRequest(position, list);

                    } else {
                        Toast.makeText(MainActivity.this, "Please turn on your location services", Toast.LENGTH_SHORT).show();
                        isLocationOn = false;
                    }
                } else {
                    if(!wifiManager.isConnectingToInternet()){
                        alertDialog = wifiManager.promptUserToTurnOnWifi();
                    }
                }
            }
        });


        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                distanceTV.setText(progress + "");
                myRadiusString = Integer.toString(progress * PROGRESS_BAR_MULTIPLIER);
                Log.e("TAG", "RADIUS STRING: " + myRadiusString);
                myRadiusDouble = (double) (progress * PROGRESS_BAR_MULTIPLIER);
                Log.e("TAG", myRadiusDouble + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void requestLocation() {
        showLocationProgressDialog();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(client, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        getLocation();
                        isLocationOn = true;
                        hideLocationProgressDialog();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            hideLocationProgressDialog();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Toast.makeText(MainActivity.this, "You can't use this app unless you have your location services turned on", Toast.LENGTH_SHORT).show();
                        hideLocationProgressDialog();
                        break;
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST) {
            ArrayList<String> results;
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = results.get(0).replace("'", "");
            autocompleteFragment.setText(text);
            autocompleteFragment.getView().findViewById(com.google.android.gms.R.id.place_autocomplete_search_input).performClick();
        }
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        isLocationOn = true;
                        getLocation();
                        hideLocationProgressDialog();
                        break;
                    case Activity.RESULT_CANCELED:
                        hideLocationProgressDialog();
                        break;
                    default:
                        hideLocationProgressDialog();
                        break;
                }
                break;
        }

    }

    private void showLocationProgressDialog() {
        if (LocationDialog == null) {
            LocationDialog = new ProgressDialog(this);
            LocationDialog.setMessage("Obtaining Location...");
            LocationDialog.setIndeterminate(true);
            LocationDialog.setCancelable(false);
            LocationDialog.setCanceledOnTouchOutside(false);
        }

        LocationDialog.show();
    }

    private void hideLocationProgressDialog() {
        if (LocationDialog != null && LocationDialog.isShowing()) {
            LocationDialog.hide();
        }
    }

    private void showRequestLoadingDialog() {
        if (requestLoadingDialog == null) {
            requestLoadingDialog = new ProgressDialog(this);
            requestLoadingDialog.setMessage("Loading...");
            requestLoadingDialog.setIndeterminate(true);
            requestLoadingDialog.setCancelable(false);
            requestLoadingDialog.setCanceledOnTouchOutside(false);
        }

        requestLoadingDialog.show();
    }

    private void hideRequestLoadingDialog() {
        if (requestLoadingDialog != null && requestLoadingDialog.isShowing()) {
            requestLoadingDialog.hide();
        }
    }


    private void executeRequest(int position, List<Category> list) {
        String type = list.get(position).getType();
        showRequestLoadingDialog();
        new RequestTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longtitude + "&radius=" + myRadiusString + "&type=" + type + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");

    }

    private void addAllCategories(List<Category> list) {

        list.add(new Category(getString(R.string.category_airport), R.drawable.airport, getString(R.string.request_type_airport)));
        list.add(new Category(getString(R.string.category_amusment_park), R.drawable.amusement, getString(R.string.request_type_amusement_park)));
        list.add(new Category(getString(R.string.category_aquarium), R.drawable.aquarium, getString(R.string.request_type_aquarium)));
        list.add(new Category(getString(R.string.category_art_gallery), R.drawable.art_gallery, getString(R.string.request_type_art_gallery)));
        list.add(new Category(getString(R.string.category_ATM), R.drawable.atm, getString(R.string.request_type_atm)));
        list.add(new Category(getString(R.string.category_bank), R.drawable.bank_dollar, getString(R.string.request_type_bank)));
        list.add(new Category(getString(R.string.category_bar), R.drawable.bar, getString(R.string.request_type_bar)));
        list.add(new Category(getString(R.string.category_bicycle_store), R.drawable.bicycle, getString(R.string.request_type_bicycle_store)));
        list.add(new Category(getString(R.string.category_bowling), R.drawable.bowling, getString(R.string.request_type_bowling)));
        list.add(new Category(getString(R.string.category_bus_station), R.drawable.bus, getString(R.string.request_type_bus_station)));
        list.add(new Category(getString(R.string.category_cafe), R.drawable.cafe, getString(R.string.request_type_cafe)));
        list.add(new Category(getString(R.string.category_camping), R.drawable.camping, getString(R.string.request_type_campgound)));
        list.add(new Category(getString(R.string.category_car_dealer), R.drawable.car_dealer, getString(R.string.request_type_car_dealer)));
        list.add(new Category(getString(R.string.category_car_rental), R.drawable.car_rental, getString(R.string.request_type_car_rental)));
        list.add(new Category(getString(R.string.category_type_car_repair), R.drawable.car_repair, getString(R.string.request_type_car_repair)));
        list.add(new Category(getString(R.string.category_casino), R.drawable.casino, getString(R.string.request_type_casino)));
        list.add(new Category(getString(R.string.category_courthouse), R.drawable.courthouse, getString(R.string.request_type_court_house)));
        list.add(new Category(getString(R.string.category_dentist), R.drawable.dentist, getString(R.string.request_type_dentist)));
        list.add(new Category(getString(R.string.category_doctor), R.drawable.doctor, getString(R.string.request_type_doctor)));
        list.add(new Category(getString(R.string.category_type_fitness), R.drawable.fitness, getString(R.string.request_type_gym)));
        list.add(new Category(getString(R.string.category_gas_station), R.drawable.gas_station, getString(R.string.request_type_gas_station)));
        list.add(new Category(getString(R.string.category_library), R.drawable.library, getString(R.string.request_type_library)));
        list.add(new Category(getString(R.string.category_police), R.drawable.police, getString(R.string.request_type_police)));
        list.add(new Category(getString(R.string.category_post_office), R.drawable.post_office, getString(R.string.request_type_post_office)));
        list.add(new Category(getString(R.string.category_restaurant), R.drawable.restaurant, getString(R.string.request_type_restaurant)));
        list.add(new Category(getString(R.string.category_school), R.drawable.school, getString(R.string.request_type_school)));
        list.add(new Category(getString(R.string.category_stadium), R.drawable.stadium, getString(R.string.request_type_staduim)));
        list.add(new Category(getString(R.string.category_train_station), R.drawable.train, getString(R.string.request_type_train_station)));
        list.add(new Category(getString(R.string.category_university), R.drawable.university, getString(R.string.request_type_university)));
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if (lastLocation != null) {
            latitude = String.valueOf(lastLocation.getLatitude());
            longtitude = String.valueOf(lastLocation.getLongitude());
            autocompleteFragment.setBoundsBias(toBounds(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), myRadiusDouble));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class RequestTask extends AsyncTask<String, Void, String> {
        private String getRequest(String address) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int status = connection.getResponseCode();
                Scanner sc = new Scanner(connection.getInputStream());
                while (sc.hasNextLine()) {
                    response.append(sc.nextLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.toString();
        }

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = getRequest(address);
            JSONObject json = null;

            try {
                json = new JSONObject(response);
                //checking if the AsyncTask is executed with a click from the category recyclerView or from the autocomplete fragment
                if (json.has("results")) {
                    JSONArray array = json.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject myobj = array.getJSONObject(i);
                        String placeID = (String) myobj.get("place_id");
                        address = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + String.valueOf(lastLocation.getLatitude()) + "," + String.valueOf(lastLocation.getLongitude()) + "&destinations=place_id:" + placeID + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4";
                        String distanceResponse = getRequest(address);

                        JSONObject distanceJson = new JSONObject(distanceResponse);
                        JSONObject distanceRows = (JSONObject) distanceJson.getJSONArray("rows").get(0);
                        JSONArray distanceElements = (JSONArray) distanceRows.getJSONArray("elements");
                        JSONObject distanceAndDuration = distanceElements.getJSONObject(0); // distance and duration
                        JSONObject distanceObj = distanceAndDuration.getJSONObject("distance");
                        JSONObject durationObj = distanceAndDuration.getJSONObject("duration");

                        JSONObject geometry = myobj.getJSONObject("geometry");
                        geometry.put("distance", distanceObj);
                        geometry.put("duration", durationObj);

                    }
                }
                if (json.has("result")) {
                    address = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + String.valueOf(lastLocation.getLatitude()) + "," + String.valueOf(lastLocation.getLongitude()) + "&destinations=place_id:" + autocompletePlaceID + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4";
                    String distanceResponse = getRequest(address);
                    JSONObject distanceJson = new JSONObject(distanceResponse);
                    JSONObject distanceRows = (JSONObject) distanceJson.getJSONArray("rows").get(0);
                    JSONArray distanceElements = distanceRows.getJSONArray("elements");
                    JSONObject distanceAndDuration = distanceElements.getJSONObject(0); // distance and duration
                    JSONObject distanceObj = distanceAndDuration.getJSONObject("distance");
                    JSONObject durationObj = distanceAndDuration.getJSONObject("duration");

                    JSONObject geometry = json.getJSONObject("result").getJSONObject("geometry");
                    geometry.put("distance", distanceObj);
                    geometry.put("duration", durationObj);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            response = json.toString();
            return response;
        }


        @Override
        protected void onPreExecute() {
            showRequestLoadingDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject jobj = null;
            Intent intent = null;
            if (!s.contains("ZERO_RESULTS")) {
                try {
                    jobj = new JSONObject(s);
                    if (jobj.has("result")) {
                        intent = new Intent(MainActivity.this, SelectedPlaceActivity.class);
                        JSONObject resultObj = jobj.getJSONObject("result");
                        placeLocation = new Location("");
                        double placeLatitude = (double) resultObj.getJSONObject("geometry").getJSONObject("location").get("lat");
                        double placeLongtitude = (double) resultObj.getJSONObject("geometry").getJSONObject("location").get("lng");
                        placeLocation.setLatitude(placeLatitude);
                        placeLocation.setLongitude(placeLongtitude);
                        distance = (String) resultObj.getJSONObject("geometry").getJSONObject("distance").get("text");
                        duration = (String) resultObj.getJSONObject("geometry").getJSONObject("duration").get("text");
                        intent.putExtra("placeLocation", placeLocation);
                        intent.putExtra("distance", distance);
                        intent.putExtra("duration", duration);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (jobj.has("results")) {
                    intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                }
                intent.putExtra("json", s);
                intent.putExtra("lastLocation", lastLocation);
                startActivity(intent);
                hideRequestLoadingDialog();
            } else {
                hideRequestLoadingDialog();
                Toast.makeText(MainActivity.this, "No places found for your criteria", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    protected void onStart() {
        if(!wifiManager.isConnectingToInternet()){
            alertDialog = wifiManager.promptUserToTurnOnWifi();
        }
        client.connect();
        super.onStart();
    }


    @Override
    protected void onStop() {
        if (alertDialog != null)
            alertDialog.hide();
        super.onStop();
    }
}
