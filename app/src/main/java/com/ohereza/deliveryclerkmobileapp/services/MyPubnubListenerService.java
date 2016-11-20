package com.ohereza.deliveryclerkmobileapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ohereza.deliveryclerkmobileapp.helper.Configs;
import com.ohereza.deliveryclerkmobileapp.other.MyApplication;
import com.ohereza.deliveryclerkmobileapp.views.NotificationActivity;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;

/**
 * Created by rkabagamba on 11/2/2016.
 */



public class MyPubnubListenerService extends IntentService {

    private SharedPreferences sharedPreferences;
    private static final String TAG_PUBNUBLISTENER = "MyPubnubListenerService";
    private PNConfiguration pnConfiguration;
    private PubNub pubnub;
    private String username;

    private LatLng clientLocation;
    private boolean zoomToClient = true;

    private PolylineOptions mPolylineOptions;

    public MyPubnubListenerService() {
        super("MyPubnubListenerService");
    }

    @Override
    public void onCreate() {

        super.onCreate();

        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(Configs.pubnub_subscribeKey);
        pnConfiguration.setPublishKey(Configs.pubnub_publishKey);
        pubnub = new PubNub(pnConfiguration);

        // Get delivery_clerk
        username = sharedPreferences.getString("usr",null);
        // Subscribe to a channel - the same as the delivery_clerk
        pubnub.subscribe().channels(Arrays.asList(username)).execute();

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        // Listen for incoming messages
       pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc
                    if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    }
                } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                if ( ! MyApplication.isMapActivityVisible() ) {
                    // Handle new message stored in message.message
                    Log.v(TAG_PUBNUBLISTENER, "message(" + message.getMessage() + ")");
                    Log.v(TAG_PUBNUBLISTENER, "MAP NOT VISIBLE");
                    // {"order_id":"f88d553b6b","type":"Delivery Request"}
                    JSONObject jsonRequest = null;

                    try {
                        jsonRequest = new JSONObject(String.valueOf(message.getMessage()));
                        Log.v(TAG_PUBNUBLISTENER, "json object: " + jsonRequest);

                        if (jsonRequest != null && jsonRequest.has("type")
                                && jsonRequest.getString("type").equalsIgnoreCase("Delivery Request")) {
                            // Handle new delivery request received
                            // launch notification activity
                            Intent intent = new Intent(MyPubnubListenerService.this, NotificationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("order_id", jsonRequest.getString("order_id"));
                            startActivity(intent);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    // if the map is visible don't handle
                    // pubnub requests from here.
                    stopSelf();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            }
        });
    }
}