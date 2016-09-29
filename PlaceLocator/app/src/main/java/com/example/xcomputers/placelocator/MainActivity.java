package com.example.xcomputers.placelocator;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    TextView testTV;
    Button but;
    String latitude;
    String longtitude;
    TextView tekstche;


    private GoogleApiClient client;
    private GoogleSignInOptions gso;
    private Location lastLocation;
    private PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTV = (TextView) findViewById(R.id.testTV);
        but = (Button) findViewById(R.id.button);

        String name = getIntent().getStringExtra("name");
        testTV.setText("Welcome " + name);

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
                new RequestTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longtitude + "&radius=" + "3000" + "&type=gas_station&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        new RequestTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longtitude+"&radius=" + "3000" + "&type=gas_station&key=AIzaSyDbS_CsfPY6AiedXdN1Cg5oY701_guJVgk");

       // Log.e("tag")
       // double latitude1 = Double.parseDouble(latitude);
      //  double longitude1 = Double.parseDouble(longtitude);




         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

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
        autocompleteFragment.setBoundsBias(toBounds(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()),3000));
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
