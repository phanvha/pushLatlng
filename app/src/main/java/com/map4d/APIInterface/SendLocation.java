package com.map4d.APIInterface;

import com.map4d.model.Post;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SendLocation {
    @FormUrlEncoded
    @POST("/api/oauth2/CreateOrTokenCustomer")
    Call<Post> loginAccount(
//            @Field("UserId") String username,
//            @Field("UserName") String password,
            @Field("Token") String token
//            @Field("Email") String email
    );
    @Headers({
            "Content-Type:application/json"
    })
    @GET("/api/api_loc.php")
    Call<Post> getlistbusstop(
            @Query("imei") String imei,
            @Query("dt") String dt,
            @Query("lat") Double lat,
            @Query("lng") Double lng,
            @Query("altitude") int altitude,
            @Query("angle") int angle,
            @Query("speed") int speed,
            @Query("loc_valid") int loc_valid,
            @Query("params") String params

    );
//    @GET("/api/BusStop/GetListBusStop")
//    Observable<Datalist> getlistbusstop(
//            @Query("page") String page,
//            @Query("size") String size,
//            @Query("search") String search
//    );
}