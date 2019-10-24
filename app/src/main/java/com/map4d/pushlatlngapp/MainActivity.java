package com.map4d.pushlatlngapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.map4d.API.APIClient;
import com.map4d.APIInterface.SendLocation;
import com.map4d.model.Post;
import com.map4d.service.LocationTracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView tvlatitude, tvlongitude;
    String imei = "BUS123001", dt = "2019-09-0809:58:00", params = "batp=100|acc=1|";
    String a ="batp",b = "=",c = "|acc", d = "1|";
    String pa = a+b+c+b+d;

    private  int altitude = 100;
    private  int angle = 45;
    private  int speed = 60;
    private  int loc_valid = 1;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvlatitude = (TextView)findViewById(R.id.lat);
        tvlongitude = (TextView) findViewById(R.id.lon);
////
//        gettime();
//        sendlocation();
        check();
    }
    //Kiem tra toa do hien tai
    private void gettime(){
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
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
    private void check() {
        CountDownTimer countDownTimer = new CountDownTimer(86400000, 3000) {
            @Override
            public void onTick(long l) {
                gettime();
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

            @Override
            public void onFinish() {
                Log.d("Failed", "Error");
            }
        };
        countDownTimer.start();
    }
}
