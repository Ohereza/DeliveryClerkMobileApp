package com.ohereza.deliveryclerkmobileapp.interfaces;

import com.ohereza.deliveryclerkmobileapp.helper.DeliveryRequestUpdater;
import com.ohereza.deliveryclerkmobileapp.helper.DeliveryRequestUpdaterResponse;
import com.ohereza.deliveryclerkmobileapp.helper.FCMInstanceUpdate;
import com.ohereza.deliveryclerkmobileapp.helper.LocationUpdateResponse;
import com.ohereza.deliveryclerkmobileapp.helper.LocationUpdater;
import com.ohereza.deliveryclerkmobileapp.helper.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @PUT("/api/resource/Delivery Request/{req_id}")
    Call<DeliveryRequestUpdaterResponse> updateDeliveryRequest(@Path("req_id") String request_id,
                                                               @Body DeliveryRequestUpdater data);

    @GET("/api/method/pds.api.start_delivering")
    Call<DeliveryRequestUpdaterResponse> startDelivery(@Query("order_number") String request_id);

    @GET("/api/method/pds.api.finish_delivering")
    Call<DeliveryRequestUpdaterResponse> endDelivery(@Query("order_number") String request_id);

    @POST("/api/resource/Location")
    Call<LocationUpdateResponse> updateLocation(@Body LocationUpdater data);

}
