package com.example.xcomputers.placelocator;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.xcomputers.placelocator.model.MyPlace;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<MyPlace> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        list = new ArrayList();


        recyclerView = (RecyclerView) findViewById(R.id.search_results_recycler_view);


        try {
            JSONObject json = new JSONObject(getIntent().getStringExtra("json"));
            JSONArray array = json.getJSONArray("results");
            Log.e("TAG", "JSON ARRAY SIZE: " + array.length());
            for(int i = 0; i < array.length(); i++){
                JSONObject myobj = array.getJSONObject(i);
                Location phoneLocation = getIntent().getParcelableExtra("lastLocation");
                Location placeLocation = new Location("");
                double placeLatitude = (double) myobj.getJSONObject("geometry").getJSONObject("location").get("lat");
                double placeLongtitude = (double) myobj.getJSONObject("geometry").getJSONObject("location").get("lng");
                placeLocation.setLatitude(placeLatitude);
                placeLocation.setLongitude(placeLongtitude);
                float distanceInKM = (phoneLocation.distanceTo(placeLocation))/1000;
                String distanceInKMString = String.format("%.2f", distanceInKM);
                if(!myobj.has("rating")){
                    list.add(new MyPlace((String) myobj.get("name"), (String) myobj.get("vicinity"), 0, (String) myobj.get("place_id"), distanceInKMString));
                }
                else{
                    String x = String.valueOf(myobj.get("rating"));
                    float rating = Float.parseFloat(x);
                    list.add(new MyPlace((String) myobj.get("name"), (String) myobj.get("vicinity"), rating, (String) myobj.get("place_id"), distanceInKMString));
                }

                Log.e("TAG", "adding to list");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "list size before setting the adapter" + list.size()+"");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchResultsRecyclerViewAdapter adapter = new SearchResultsRecyclerViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.WHITE).build());
        adapter.setOnResultClickListener(new SearchResultsRecyclerViewAdapter.onResultClickListener() {
            @Override
            public void onResultClicked(View view, int position) {
                String placeID = list.get(position).getID();
                Log.e("TAG", "onClick Item from list " + placeID);
                new RequestTask().execute("https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeID + "&key=AIzaSyDWeC1Uu7iVM2HyHi-dc6Xvde6b45vSFl4");
            }
        });
    }

    private class RequestTask extends AsyncTask<String, Void, String> {

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
                File file = new File("logche.txt");
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                while(sc.hasNextLine()){
                    response += sc.nextLine();
                    writer.append(response);
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
            //TODO I've passed the request for place details according to the ID of the clicked place and I've put the json responce in the intent. Just put your activity name
            //TODO in the intent and take the json from the intent in your activity
             Intent intent = new Intent(SearchResultsActivity.this, SelectedPlaceActivity.class);
            intent.putExtra("json", s);
            startActivity(intent);

            Log.e("TAG", s);

            // Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

        }

    }

}
