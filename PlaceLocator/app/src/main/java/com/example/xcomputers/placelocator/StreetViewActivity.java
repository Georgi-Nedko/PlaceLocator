package com.example.xcomputers.placelocator;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup.LayoutParams;

public class StreetViewActivity extends AppCompatActivity {

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    private StreetViewPanoramaView mStreetViewPanoramaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        if (savedInstanceState == null) {
            options.position(SYDNEY);
        }

        mStreetViewPanoramaView = new StreetViewPanoramaView(this, options);
        addContentView(mStreetViewPanoramaView,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mStreetViewPanoramaView.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        mStreetViewPanoramaView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mStreetViewPanoramaView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mStreetViewPanoramaView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStreetViewPanoramaView.onSaveInstanceState(outState);
    }
}
