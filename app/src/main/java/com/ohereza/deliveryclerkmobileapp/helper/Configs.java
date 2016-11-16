package com.ohereza.deliveryclerkmobileapp.helper;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

/**
 * Created by rkabagamba on 9/28/2016.
 */

public class Configs {
    public static final String PREFS_NAME = "MyPrefsFile";

    // related to the server connector class
    public static String serverAddress = "http://pds.intego.rw:8000";
    public static String loginRelatedUri = "/api/method/login";
    public static String updateFcmInstanceIdUri = "/api/resource/User";
    public static String updateLocationUri = "/api/resource/Location";
    public static String updateDeliveryRequestStatusUri = "/api/resource/Delivery Request/";

    // related to PubNub
    public static String pubnub_subscribeKey = "sub-c-266bcbc0-9884-11e6-b146-0619f8945a4f";
    public static String pubnub_publishKey = "pub-c-21663d8a-850d-4d99-adb3-3dda55a02abd";


    // Pubnub Object
    public static PubNub pubnub = new PubNub(new PNConfiguration()
                                                .setSubscribeKey(Configs.pubnub_subscribeKey)
                                                .setPublishKey(Configs.pubnub_publishKey));

}
