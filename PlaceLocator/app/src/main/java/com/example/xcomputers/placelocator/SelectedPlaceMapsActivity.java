package com.example.xcomputers.placelocator;


import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectedPlaceMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final float MAP_ZOOM = 14.0f;
    private Button streetView;
    private Location location;
    private LatLng place;
    private StreetViewPanoramaView mStreetViewPanoramaView;
    private Handler mHandler = new Handler();
    private static final int QUERY_DELAY_MS = 500;
    private boolean isThereStreetViewAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_place_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        location = getIntent().getParcelableExtra("placeLocation");
        place = new LatLng(location.getLatitude(), location.getLongitude());
        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        if (savedInstanceState == null) {
            options.position(place);
        }
        mStreetViewPanoramaView = new StreetViewPanoramaView(this, options);
        mStreetViewPanoramaView.onCreate(savedInstanceState);

        mStreetViewPanoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (streetViewPanorama.getLocation() == null) {
                            isThereStreetViewAvailable = false;
                        }
                    }
                }, QUERY_DELAY_MS);
            }
        });
        streetView = (Button) findViewById(R.id.streetViewButton);
        streetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThereStreetViewAvailable) {
                    Intent intent = new Intent(SelectedPlaceMapsActivity.this, StreetViewActivity.class);
                    intent.putExtra("place", place);
                    startActivity(intent);
                } else {
                    Toast.makeText(SelectedPlaceMapsActivity.this, "There is no street view available for this location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions().position(place).title(getIntent().getStringExtra("name"));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, MAP_ZOOM));

    }


}

