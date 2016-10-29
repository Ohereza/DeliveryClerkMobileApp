package com.ohereza.deliveryclerkmobileapp.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by rkabagamba on 10/12/2016.
 */

public class FCMInstanceUpdate {

    @Expose
    public String fcm_instance_id;


    public FCMInstanceUpdate(String instanceId) {
        fcm_instance_id = instanceId;

    }

    private String getJsonString() {
        // Before converting to GSON check value of id
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(this);
    }

}
