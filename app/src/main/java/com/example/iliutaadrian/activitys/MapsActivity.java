package com.example.iliutaadrian.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    //TODO add permanent storage 2 modes
    //TODO add search on map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void centerMapOnLocation(Location location, String title){
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        if(!title.equals("0"))
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,1,locationListener);
                Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnowLocation, "0");
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList =  geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList.size()>0){
                mMap.addMarker(new MarkerOptions().position(latLng).title(addressList.get(0).getAddressLine(0)));

                MainActivity.latLngs.add(latLng);
                MainActivity.destinations.add(addressList.get(0).getAddressLine(0)+" "+addressList.get(0).getCountryName());
                MainActivity.arrayAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Location saved", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkPermision(String title){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1,locationListener);
            Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centerMapOnLocation(lastKnowLocation, title);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Intent intent = getIntent();
        int aux = intent.getIntExtra("address",1);

        if(aux==0) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerMapOnLocation(location, "0");
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) { }
                @Override
                public void onProviderEnabled(String s) {  }
                @Override
                public void onProviderDisabled(String s) {  }
            };
            checkPermision("0");
        }
        else if(aux==1){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerMapOnLocation(location, "My current location");
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) { }
                @Override
                public void onProviderEnabled(String s) { }
                @Override
                public void onProviderDisabled(String s) { }
            };
            checkPermision("My current location");
        }
        else{
            Location select = new Location(LocationManager.GPS_PROVIDER);
            select.setLatitude(MainActivity.latLngs.get(aux-2).latitude);
            select.setLongitude(MainActivity.latLngs.get(aux-2).longitude);

            centerMapOnLocation(select, MainActivity.destinations.get(aux));
        }
    }
}
