package com.example.xcomputers.placelocator;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.xcomputers.placelocator.model.MyPlace;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                if(!myobj.has("rating")){
                    list.add(new MyPlace((String) myobj.get("name"), (String) myobj.get("vicinity"), 0));
                }
                else{
                    String x = String.valueOf(myobj.get("rating"));
                    float rating = Float.parseFloat(x);
                    list.add(new MyPlace((String) myobj.get("name"), (String) myobj.get("vicinity"), rating));
                }

                Log.e("TAG", "adding to list");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "list size before setting the adapter" + list.size()+"");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SearchResultsRecyclerViewAdapter(this, list));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.WHITE).build());

    }

}
