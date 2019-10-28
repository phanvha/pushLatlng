package com.map4d.pushlatlngapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.map4d.API.APIClient;
import com.map4d.APIInterface.SendLocation;
import com.map4d.SQLiteDatabaseHelper.DatabaseHelper;
import com.map4d.model.Data;
import com.map4d.model.Post;
import com.map4d.service.ConnectionReceiver;
import com.map4d.service.LocationTracker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView tvlatitude, tvlongitude;
    String imei = "BUS123001", dt = "2019-09-0809:58:00", params = "batp=100|acc=1|";
    String a ="batp",b = "=",c = "|acc", d = "1|";
    String pa = a+b+c+b+d;
    LocationManager locationManager;
    Location location;
    private final int REQUEST_LOCATION = 200;
    private static final String TAG = "MainActivity";

    private  int altitude = 100;
    private  int angle = 45;
    private  int speed = 60;
    private  int loc_valid = 1;
    private Double latitude, lat, longitude, lon;
    boolean ret = ConnectionReceiver.isConnected();
    private DatabaseHelper db;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvlatitude = (TextView)findViewById(R.id.lat);
        tvlongitude = (TextView) findViewById(R.id.lon);

        checkConnect();
//upate word
//        Word word = db.getAllWord().get(0);
//        word.setMean("Sách(n), đặt chổ(v), đặt vé(v)");
//        db.updateWord(word);
//
//
//        //loge all word in database
//        for(Word w: db.getAllWord()){
//            Log.e(TAG, "onCreate: " + w.getId() + ", " + w.getWord() + ", " + w.getMean());
//        }
//
//        //delete word
//        db.deleteWord(db.getAllWord().get(0));
//
        //loge total rows in database
}

    private void getlocationandSaveToSQLite(){
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
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
                lat = location.getLatitude();
                lon = location.getLongitude();
//                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
            showGPSDisabledAlertToUser();
        }
    }
    private void checkConnect() {
        CountDownTimer countDownTimer = new CountDownTimer(86400000, 1000) {
            @Override
            public void onTick(long l) {
                if (ret == true) {
                    msg = "Thiết bị đã kết nối internet";
                    check();

                } else {
                    msg = "Thiết bị chưa kết nối internet";
                    getlocationandSaveToSQLite();
                    db = DatabaseHelper.getInstance(MainActivity.this);
                    if (ret = true){
                        db.insertLocation(new Data(1, "on",latitude, longitude ));
                    }else{
                        db.insertLocation(new Data(1, "off",latitude, longitude ));
                    }
                    //insert data
                    //loge all word in database
                    for(Data w: db.getAllLocation()){
                        Log.e(TAG, "onCreate: " + w.getId() + ", " + w.getStatus() + ", " + w.getLatitude()+", "+ w.getLongitude());
                    }
                    Log.e(TAG, "onCreate: " + db.getTotalLocation());
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

        LocationTracker tracker1 = new LocationTracker(MainActivity.this);
        tvlatitude.setText("Vĩ độ: " + tracker1.getLatitude());
        tvlongitude.setText("KInh độ: " + tracker1.getLongitude());
        latitude = tracker1.getLatitude();
        longitude = tracker1.getLongitude();
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
    //Kiem tra toa do hien tai và gửi tọa độ lên server 5s/lần
    private void check() {
        CountDownTimer countDownTimer = new CountDownTimer(86400000, 5000) {
            @Override
            public void onTick(long l) {
                gettime();
                db = DatabaseHelper.getInstance(MainActivity.this);
                List<Data> list = db.getData("off");
                for (Data item : list) {

                    latitude = item.getLatitude();
                    Log.e(TAG, latitude.toString());
                    longitude = item.getLongitude();
                    Log.e(TAG, longitude.toString());
                    SendLocation service = APIClient.getClient().create(SendLocation.class);
                    Call<Post> userCall = service.getlistbusstop(imei, dt, latitude, longitude, altitude, angle, speed, loc_valid, params);

                    userCall.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            //onSignupSuccess();
                            if (response.isSuccessful()) {
                                Log.d("onResponse", "Đã gửi dữ liệu thành công");
                                Toast.makeText(getApplicationContext(), "Đã gửi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("onResponse", "Dữ liệu chưa được gửi: ");
                            }
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("Failed: ", t.toString());
                        }
                    });
                }
                SendLocation service = APIClient.getClient().create(SendLocation.class);
                Call<Post> userCall = service.getlistbusstop(imei, dt, lat, lon, altitude, angle, speed, loc_valid, params);

                userCall.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        //onSignupSuccess();
                        if (response.isSuccessful()) {
                            Log.d("onResponse", "Đã gửi dữ liệu thành công");
                            Toast.makeText(getApplicationContext(), "Đã gửi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("onResponse", "Dữ liệu chưa được gửi: ");
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Log.d("Failed: ", t.toString());
                    }
                });

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
        tvlatitude.setText(String.valueOf(location.getLatitude()));
        tvlongitude.setText(String.valueOf(location.getLongitude()));
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
