package com.map4d.API;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static String baseURL ="http://s1.gpsserver.vn";

    //1/ API GET, gởi tọa độ lat, lng lên
    //http://s1.gpsserver.vn/api/api_loc.php?imei=BUS123001&dt=2019-09-0809:58:00&lat=16.091050&lng=108.227800&altitude=100&angle=45&speed=60&loc_valid=1&params=batp=100|acc=1|
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                interceptor.intercept(chain);
                Request request = chain.request();
                String string = request.url().toString();
                string = string.replace("%26", "=");
                string = string.replace("%3D", "=");
                Request newRequest = new Request.Builder()
                        .url(string)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;

    }

}