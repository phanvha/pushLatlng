package com.map4d.pushlatlngapp;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import vn.map4d.map4dsdk.camera.MFCameraUpdateFactory;
import vn.map4d.map4dsdk.maps.LatLng;
import vn.map4d.map4dsdk.maps.MFMapView;
import vn.map4d.map4dsdk.maps.Map4D;
import vn.map4d.map4dsdk.maps.OnMapReadyCallback;

public class BubbleActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private static final int REQUEST_LOCATION = 89;
    private Map4D map4D;
    private MFMapView mapView;
    TextView textView;
    LocationManager locationManager;
    Location location;
    private Double latitude, lat, longitude, lon;
    private LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView = findViewById(R.id.map2D);
        mapView.getMapAsync(this);


        textView = findViewById(R.id.textView);

        FloatingActionButton fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
    private void getlocation(){
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BubbleActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("lat: ",latitude.toString());
                Log.d("lon: ",longitude.toString());
                //                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().getExtras() != null) {

            String value = getIntent().getStringExtra("key");
            textView.setText(value);
        }
    }

    @Override
    public void onMapReady(Map4D map4D) {
        this.map4D = map4D;
        map4D.setMinZoomPreference(3.f);
        map4D.setMaxZoomPreference(19.f);
        getlocation();
        latLng = new LatLng(latitude, longitude);
        map4D.animateCamera(MFCameraUpdateFactory.newLatLngZoom(latLng,16.0f));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        map4D.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

