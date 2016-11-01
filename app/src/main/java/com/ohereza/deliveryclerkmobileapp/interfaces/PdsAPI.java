package com.ohereza.deliveryclerkmobileapp.interfaces;

import com.ohereza.deliveryclerkmobileapp.helper.FCMInstanceUpdate;
import com.ohereza.deliveryclerkmobileapp.helper.LocationUpdateResponse;
import com.ohereza.deliveryclerkmobileapp.helper.LocationUpdater;
import com.ohereza.deliveryclerkmobileapp.helper.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by rkabagamba on 10/9/2016.
 */

public interface PdsAPI {
    @FormUrlEncoded
    @POST("/api/method/login")
    Call<LoginResponse> login(@Field("usr") String username, @Field("pwd") String password);

    @FormUrlEncoded
    @PUT("/api/resource/User/administrator")
    Call<Void> updateFirebaseInstanceId(@Field("fcm_instance_id") String instanceId);

    @PUT("/api/resource/User/administrator")
    Call<FCMInstanceUpdate> updateFCMInstanceId(@Body FCMInstanceUpdate data);

    @POST("/api/resource/Location")
    Call<LocationUpdateResponse> updateLocation(@Body LocationUpdater data);


}
