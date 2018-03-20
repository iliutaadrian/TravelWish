package com.example.iliutaadrian.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> destinations = new ArrayList<>();
    static ArrayList<LatLng> latLngs = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);


        destinations.add("           Add new Destinations or tap on list");
        destinations.add("                         My current location");
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, destinations);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0) {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("address", i);

                    startActivity(intent);
                }
            }
        });
    }

    public void addDestination(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("address",0);
        startActivity(intent);
    }
}
