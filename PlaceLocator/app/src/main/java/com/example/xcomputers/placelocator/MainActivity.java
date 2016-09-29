package com.example.xcomputers.placelocator;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xcomputers.placelocator.model.Category;
import com.example.xcomputers.placelocator.model.MyPlace;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    TextView testTV;
    Button but;
    String latitude;
    String longtitude;
    RecyclerView categoriesView;
    TextView distanceTV;
    TextView selectDistanceTV;
    TextView distanceKMTV;
    SeekBar distanceSeekBar;

    private String myRadiusString;
    private double myRadiusDouble;
    private GoogleApiClient client;
    private GoogleSignInOptions gso;
    private Location lastLocation;
    private PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
       // testTV = (TextView) findViewById(R.id.testTV);
        but = (Button) findViewById(R.id.button);
        but.setTypeface(custom_font);
        String name = getIntent().getStringExtra("name");
        //testTV.setText("Welcome " + name);

        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(LocationServices.API)
                    .build();
        }

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longtitude + "&radius=" + myRadiusString + "&type=gas_station&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        categoriesView = (RecyclerView) findViewById(R.id.recycler_view);
        List<Category> list = new ArrayList<>();
        addAllCategories(list);

        categoriesView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, list);
        categoriesView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, "clicked" + position, Toast.LENGTH_SHORT).show();
            }
        });

        selectDistanceTV = (TextView) findViewById(R.id.distance_left_TV);
        distanceKMTV = (TextView) findViewById(R.id.distance_right_TV);
        selectDistanceTV.setTypeface(custom_font);
        distanceKMTV.setTypeface(custom_font);

        distanceSeekBar = (SeekBar) findViewById(R.id.distance_seek_bar);
        distanceTV = (TextView) findViewById(R.id.distance_middle_TV);

        distanceTV.setText("0");
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceTV.setText(progress + "");
                myRadiusString = new Integer(progress*1000).toString();
                myRadiusDouble = (double) (progress * 1000);
                Log.e("TAG", myRadiusDouble+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void addAllCategories(List list){

        list.add(new Category("Airport", R.drawable.airport));
        list.add(new Category("Amusement park", R.drawable.amusement));
        list.add(new Category("Aquarium", R.drawable.aquarium));
        list.add(new Category("Art gallery", R.drawable.art_gallery));
        list.add(new Category("ATM", R.drawable.atm));
        list.add(new Category("Bank", R.drawable.bank_dollar));
        list.add(new Category("Bar", R.drawable.bar));
        list.add(new Category("Bicycle store", R.drawable.bicycle));
        list.add(new Category("Bowling", R.drawable.bowling));
        list.add(new Category("Bus station", R.drawable.bus));
        list.add(new Category("Cafe", R.drawable.cafe));
        list.add(new Category("Camping", R.drawable.camping));
        list.add(new Category("Car dealer", R.drawable.car_dealer));
        list.add(new Category("Car rental", R.drawable.car_rental));
        list.add(new Category("Car repair", R.drawable.car_repair));
        list.add(new Category("Casino", R.drawable.casino));
        list.add(new Category("Courthouse", R.drawable.courthouse));
        list.add(new Category("Dentist", R.drawable.dentist));
        list.add(new Category("Doctor", R.drawable.doctor));
        list.add(new Category("Fitness", R.drawable.fitness));
        list.add(new Category("Gas station", R.drawable.gas_station));
        list.add(new Category("Library", R.drawable.library));
        list.add(new Category("Police", R.drawable.police));
        list.add(new Category("Post office", R.drawable.post_office));
        list.add(new Category("Restaurant", R.drawable.restaurant));
        list.add(new Category("School", R.drawable.school));
        list.add(new Category("Stadium", R.drawable.stadium));
        list.add(new Category("Train station", R.drawable.train));
        list.add(new Category("University", R.drawable.university));
    }
    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if(lastLocation != null){
            latitude = String.valueOf(lastLocation.getLatitude());
            longtitude = String.valueOf(lastLocation.getLongitude());
        }
        autocompleteFragment.setBoundsBias(toBounds(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()),myRadiusDouble));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                while(sc.hasNextLine()){
                    response += sc.nextLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.e("TAG", s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStart() {
        client.connect();
        super.onStart();
    }
}
