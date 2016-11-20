package com.ohereza.deliveryclerkmobileapp.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by rkabagamba on 10/13/2016.
 */

public class LocationUpdater {

    @Expose
    public String type;
    @Expose
    public String longitude;
    @Expose
    public String latitude;
    @Expose
    public String delivery_clerk;


    public LocationUpdater( String mtype, String mUsername, String mlongitude, String mlatitude) {

        type = mtype;
        delivery_clerk = mUsername;
        longitude = mlongitude;
        latitude = mlatitude;

    }


    private String getJsonString() {
        // Before converting to GSON check value of id
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(this);
    }

}
