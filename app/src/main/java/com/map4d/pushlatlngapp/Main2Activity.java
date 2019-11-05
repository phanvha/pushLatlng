package com.map4d.pushlatlngapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.map4d.API.APIClient;
import com.map4d.APIInterface.SendLocation;
import com.map4d.SQLiteDatabaseHelper.DatabaseHelper;
import com.map4d.model.Data;
import com.map4d.model.Post;
import com.map4d.service.ConnectionReceiver;
import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final int MY_PERMISSION = 1000;
    private static final String CHANNEL_ID = "channel";
    TextView tvlatitude, tvlongitude;
    private String imei = "BUS123001", dt = "2019-09-0809:58:00", params = "batp=100|acc=1|";
    LocationManager locationManager;
    Location location;
    private final int REQUEST_LOCATION = 200;
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private int altitude = 100;
    private int angle = 45;
    private int speed = 60;
    private int loc_valid = 1;
    private Double latitude, lat, longitude, lon;
    boolean ret = ConnectionReceiver.isConnected();
    private DatabaseHelper db;
    private String msg;
    private BubblesManager bubblesManager;
    private NotificationBadge manager;
    NotificationManager notificationManager;
    Notification.Builder builder;
    NotificationChannel channel;
    private PendingIntent contentIntent;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addNewBubble();
            }
        });
//        Button btnadd = (Button)findViewById(R.id.fab);
//        btnadd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addNewBubble();
//            }
//        });
        //check permission
        if (Build.VERSION.SDK_INT >=23) {
            if (!Settings.canDrawOverlays(Main2Activity.this)) {
                Intent intentt = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package: " + getPackageName()));
                startActivityForResult(intentt, MY_PERMISSION);
            }
        }
//        }else {
//            Intent intent  = new Intent(Main2Activity.this, Service.class);
//            startActivity(intent);
//        }
        // Create bubble intent
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = "My Channel";
        String description = "xyz";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setAllowBubbles(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        tvlatitude = (TextView)findViewById(R.id.lat);
        tvlongitude = (TextView) findViewById(R.id.lon);
//        getlocation();
//        checkConnect();
//
//        sendlocation();
        initBubble();
    }

    private void addNewBubble2() {
        // Create bubble intent
        Intent target = new Intent(this, BubbleActivity.class);
        PendingIntent bubbleIntent =
                PendingIntent.getActivity(this, 0, target, 0 /* flags */);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        // Create bubble metadata
        Notification.BubbleMetadata bubbleData =
                new Notification.BubbleMetadata.Builder()
                        .setDesiredHeight(600)
                        .setIcon(Icon.createWithResource(this, R.drawable.avatar))
                        .setIntent(bubbleIntent)
                        .build();

// Create notification
        Person chatBot = new Person.Builder()
                .setBot(true)
                .setName("BubbleBot")
                .setImportant(true)
                .build();

        Notification.Builder builder =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_small_icon)
                        .setBubbleMetadata(bubbleData)
                        .addPerson(chatBot);

    }


    }

    private void initBubble() {
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_remove)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        addNewBubble();
                    }
                }).build();
        bubblesManager.initialize();

    }

    private void addNewBubble() {
        final BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(Main2Activity.this)
                .inflate(R.layout.bubbles_layout,null);
        manager = (NotificationBadge)bubbleLayout.findViewById(R.id.badge);

        manager.setNumber(88);
        bubbleLayout.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                Toast.makeText(getApplicationContext(),"Removed",Toast.LENGTH_SHORT).show();

            }
        });
        bubbleLayout.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                Intent intenttt = new Intent(Main2Activity.this, BubbleActivity.class);
                startActivity(intenttt);
                finish();
                Log.d("small icon", "clicked!!!");
            }
        });
        bubbleLayout.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleLayout,60,20);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            addNewBubble2();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getlocation(){
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                tvlatitude.setText(String.valueOf(location.getLatitude()));
                tvlongitude.setText(String.valueOf(location.getLongitude()));
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("lat: ",latitude.toString());
                Log.d("lon: ",longitude.toString());
                //                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
            showGPSDisabledAlertToUser();
        }
    }
    private void checkConnect() {
        CountDownTimer countDownTimer = new CountDownTimer(86400000, 100) {
            @Override
            public void onTick(long l) {
                getlocation();
                gettime();
                db = DatabaseHelper.getInstance(Main2Activity.this);
                if (ret == true) {
                    Log.d("time ",dt);
                    db.insertLocation(new Data(1,"on",latitude, longitude,dt));
                        for(Data w: db.getAllLocation()){
                            Log.e(TAG, "onCreate: on " + w.getId() + ", " + w.getStatus() + ", " + w.getLatitude()+", "+ w.getLongitude()+","+w.getTime());
                        }

                } else {
                    db.insertLocation(new Data(1,"off",latitude, longitude,dt));
                    //insert data
                    //loge all word in database
                    for(Data w: db.getData("off")){
                        Log.e(TAG, "onCreate: off " + w.getId() + ", " + w.getStatus() + ", " + w.getLatitude()+", "+ w.getLongitude());
                    }
//
                }
            }

            @Override
            public void onFinish() {
                Log.d("Failed", "Error");
            }
        };
        countDownTimer.start();
    }
    private void gettime(){
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        dt = dateFormatter.format(today);
    }
    private void sendlocation(){
        CountDownTimer countDownTimer = new CountDownTimer(86400000, 1000) {
            @Override
            public void onTick(long l) {
                gettime();
                getlocation();
                db = DatabaseHelper.getInstance(Main2Activity.this);
//                        LocationTracker tracker1 = new LocationTracker(MainActivity.this);
//                        tvlatitude.setText("Vĩ độ: " + tracker1.getLatitude());
//                        tvlongitude.setText("KInh độ: " + tracker1.getLongitude());
//                        latitude = tracker1.getLatitude();
//                        longitude = tracker1.getLongitude();
                if (latitude != 0.0 || longitude != 0.0 ){
                    SendLocation service = APIClient.getClient().create(SendLocation.class);
                    Call<Post> userCall = service.getlistbusstop(imei,dt,latitude,longitude,altitude,angle,speed,loc_valid,params);

                    userCall.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            //onSignupSuccess();
                            if (response.isSuccessful()) {
                                Log.d("onResponse", "" + response.body().toString());
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("Failed: ", t.toString());
                        }
                    });
                }
                //get location in sqlite
                List<Data> list = db.getData("off");
                if (list.size() != 0){
                    for (Data item : list) {
                        lat = item.getLatitude();
                        lon = item.getLongitude();
                        if (lat != 0.0 && lon != 0.0 ){
                            SendLocation service = APIClient.getClient().create(SendLocation.class);
                            Call<Post> userCall = service.getlistbusstop(imei,dt,lat,lon,altitude,angle,speed,loc_valid,params);

                            userCall.enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(Call<Post> call, Response<Post> response) {
                                    //onSignupSuccess();
                                    if (response.isSuccessful()) {
                                        Log.d("onResponse", "" + response.body().toString());
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Post> call, Throwable t) {
                                    Log.d("Failed: ", t.toString());
                                }
                            });
                        }
                        for (int i = 0; i<= list.size();i++){
                            db.updateLocation(new Data(i, "on",latitude,longitude,dt));

                        }
                    }
                }

            }

            @Override
            public void onFinish() {
                Log.d("Failed", "Error");
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onLocationChanged(Location location) {
        tvlatitude.setText("latitude: "+String.valueOf(location.getLatitude()));
        tvlongitude.setText("longitude: "+String.valueOf(location.getLongitude()));
        //        latitude = location.getLatitude();
        //        longitude = location.getLongitude();
        //        getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        //        if (s.equals(LocationManager.GPS_PROVIDER)) {
        //            showGPSDisabledAlertToUser();
        //        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    //    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
    //        Thread thread = new Thread() {
    //            @Override public void run() {
    //                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    //                String result = null;
    //                try {
    //                    List<Address> list = geocoder.getFromLocation(
    //                            location.getLatitude(), location.getLongitude(), 1);
    //                    if (list != null && list.size() > 0) {
    //                        Address address = list.get(0);
    //                        // sending back first address line and locality
    //                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " +  address.getCountryName() ;
    //                    }
    //                } catch (IOException e) {
    //                    Log.e(TAG, "Impossible to connect to Geocoder", e);
    //                } finally {
    //                    Message msg = Message.obtain();
    //                    msg.setTarget(handler);
    //                    if (result != null) {
    //                        msg.what = 1;
    //                        Bundle bundle = new Bundle();
    //                        bundle.putString("address", result);
    //                        msg.setData(bundle);
    //                    } else
    //                        msg.what = 0;
    //                    msg.sendToTarget();
    //                }
    //            }
    //        };
    //        thread.start();
    //    }
    //    private class GeoCoderHandler extends Handler {
    //        @Override
    //        public void handleMessage(Message message) {
    //            String result;
    //            switch (message.what) {
    //                case 1:
    //                    Bundle bundle = message.getData();
    //                    result = bundle.getString("address");
    //                    break;
    //                default:
    //                    result = null;
    //            }
    //            currentCity.setText(result);
    //        }
    //    }
}

