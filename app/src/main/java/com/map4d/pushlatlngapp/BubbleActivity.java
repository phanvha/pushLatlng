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
import android.view.Menu;
import android.view.MenuItem;
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
    private String imei = "BUS123001", dt = "2019-09-0809:58:00",hour ="01:30:00",day = "2019-09-09", params = "batp=100|acc=1|";
    private int altitude = 100;
    private int angle = 45;
    private int speed = 60;
    private int loc_valid = 1;
    private TextView tvstreet, tvaddress, tvnamebus, tvspeed, tvaltitude, tvangle, tvtime, tvLatitude, tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mapView = findViewById(R.id.map2D);
        mapView.getMapAsync(this);


        textView = findViewById(R.id.textView);
        tvnamebus = (TextView) findViewById(R.id.tvnamebus);
        tvangle = (TextView) findViewById(R.id.tvangle);
        tvLatitude = (TextView) findViewById(R.id.tvlatitude);
        tvLongitude = (TextView) findViewById(R.id.tvlongitude);
        tvaltitude = (TextView)findViewById(R.id.tvaltitude);
        tvtime = (TextView) findViewById(R.id.tvtime);
        tvspeed = (TextView) findViewById(R.id.tvspeed);

        tvnamebus.setText("Tên xe: "+imei);
        tvangle.setText("Góc nhìn: "+String.valueOf(angle)+" độ");
        tvaltitude.setText("Độ cao: "+String.valueOf(altitude)+" mét");
        tvspeed.setText("Tốc độ: "+String.valueOf(speed)+" km/h");
        tvtime.setText("Thời gian: "+dt);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menububble, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_choiseMap) {

        }

        return super.onOptionsItemSelected(item);
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
                tvLatitude.setText("Vĩ độ: "+String.valueOf(location.getLatitude()));
                tvLongitude.setText("Kinh độ: "+String.valueOf(location.getLongitude()));
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
