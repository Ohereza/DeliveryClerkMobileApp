package com.ohereza.deliveryclerkmobileapp.services;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/**
 * Created by rkabagamba on 11/2/2016.
 */

public class MyPubnubListenerService extends SubscribeCallback {
    private static final String TAG_PUBNUB = "pubnub";

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
    }
    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        Log.v(TAG_PUBNUB, "OnChannel: "+message.getChannel()+" message("+message.getMessage()+ ")");

        if (message.getChannel().toString().equalsIgnoreCase("deliveryRequests")){
            Log.v(TAG_PUBNUB, "On delivery requests handling");



        }else if(message.getChannel().toString().equalsIgnoreCase("mymaps")){


        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        // no presence handling for simplicity
    }
}


