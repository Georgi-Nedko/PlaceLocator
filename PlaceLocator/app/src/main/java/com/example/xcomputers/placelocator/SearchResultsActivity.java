package com.example.xcomputers.placelocator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.xcomputers.placelocator.model.MyPlace;
import com.example.xcomputers.placelocator.model.WifiManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<MyPlace> list;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private SearchResultsRecyclerViewAdapter adapter;
    private ProgressDialog mProgressDialog;
    private Location phoneLocation;
    private Location placeLocation;
    private double result;
    private JSONObject myobj;
    private String distance;
    private String duration;
    private AlertDialog alertDialog;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = WifiManager.getInstance(this);
        setContentView(R.layout.activity_search_results);
        list = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.search_results_recycler_view);
        try {
            JSONObject json = new JSONObject(getIntent().getStringExtra("json"));
            JSONArray array = json.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                myobj = array.getJSONObject(i);
                phoneLocation = getIntent().getParcelableExtra("lastLocation");
                placeLocation = new Location("");
                double placeLatitude = (double) myobj.getJSONObject("geometry").getJSONObject("location").get("lat");
                double placeLongtitude = (double) myobj.getJSONObject("geometry").getJSONObject("location").get("lng");
                placeLocation.setLatitude(placeLatitude);
                placeLocation.setLongitude(placeLongtitude);

                distance = (String) myobj.getJSONObject("geometry").getJSONObject("distance").get("text");
                duration = (String) myobj.getJSONObject("geometry").getJSONObject("duration").get("text");

                if (!myobj.has("rating")) {
                    list.add(new MyPlace((String) myobj.get("name"), (String) myobj.get("vicinity"), 0, (String) myobj.get("place_id"), placeLocation, distance, duration));
                } else {
                    String x = String.valueOf(myobj.get("rating"));
                    float rating = Float.parseFloat(x);
                    list.add(new MyPlace((String) myobj.get("name"), (String) myobj.get("vicinity"), rating, (String) myobj.get("place_id"), placeLocation, distance, duration));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Collections.sort(list, new Comparator<MyPlace>() {
            @Override
            public int compare(MyPlace o1, MyPlace o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsRecyclerViewAdapter(SearchResultsActivity.this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.WHITE).build());
        adapter.setOnResultClickListener(createClickListener());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.SearchActivityTabName));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.SearchActivityTabDistance));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.SearchActivityTabRating));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                Collections.sort(list, new Comparator<MyPlace>() {
                    @Override
                    public int compare(MyPlace o1, MyPlace o2) {
                        switch (tab.getPosition()) {
                            case 0:
                                return o1.getName().compareTo(o2.getName());
                            case 1:
                                result = ((Double.parseDouble(o1.getDistanceToPhone().split(" ")[0])) * 1000) - (Double.parseDouble(o2.getDistanceToPhone().split(" ")[0]) * 1000);
                                return (int) result;
                            case 2:
                            default:
                                return o1.getRating() < o2.getRating() ? 1
                                        : o1.getRating() > o2.getRating() ? -1
                                        : 0;
                        }
                    }

                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading_dialog));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    protected SearchResultsRecyclerViewAdapter.onResultClickListener createClickListener() {
        return new SearchResultsRecyclerViewAdapter.onResultClickListener() {
            @Override
            public void onResultClicked(View view, int position) {
                if (wifiManager.isConnectingToInternet()) {
                    String placeID = list.get(position).getID();
                    distance = list.get(position).getDistanceToPhone();
                    duration = list.get(position).getDuration();
                    placeLocation = list.get(position).getLocation();
                    new RequestTask().execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");
                } else {
                    if(!wifiManager.isConnectingToInternet()){
                        alertDialog = wifiManager.promptUserToTurnOnWifi();
                    }
                }
            }
        };
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
                while (sc.hasNextLine()) {
                    response += sc.nextLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {

            Intent intent = new Intent(SearchResultsActivity.this, SelectedPlaceActivity.class);
            intent.putExtra("json", s);
            intent.putExtra("lastLocation", phoneLocation);
            intent.putExtra("placeLocation", placeLocation);
            intent.putExtra("distance", distance);
            intent.putExtra("duration", duration);
            startActivity(intent);
            hideProgressDialog();

        }
    }
}

