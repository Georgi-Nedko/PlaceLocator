package com.example.xcomputers.placelocator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView testTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTV = (TextView) findViewById(R.id.testTV);
        String name = getIntent().getStringExtra("name");
        testTV.setText("Welcome " + name);

    }
}