package com.ohereza.deliveryclerkmobileapp.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by rkabagamba on 11/6/2016.
 */

public class DeliveryRequestUpdater {

    @Expose
    public String status;

    public DeliveryRequestUpdater( String newStatus) {
        status = newStatus;
    }

    private String getJsonString() {
        // Before converting to GSON check value of id
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(this);
    }
}
