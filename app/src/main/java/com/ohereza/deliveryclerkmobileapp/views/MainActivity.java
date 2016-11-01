package com.ohereza.deliveryclerkmobileapp.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.interfaces.PdsAPI;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;


public class MainActivity extends AppCompatActivity {

    private ClearableCookieJar cookieJar;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private PdsAPI pdsAPI;
    private Location mCurrentLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int gpsStatus = 0;

        try {
            gpsStatus = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if(gpsStatus==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NOTICE");
            builder.setMessage("Please enable GPS to allow tracking of your location");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Prompt to enable location.
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);
                }
            });

            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setSubscribeKey("sub-c-266bcbc0-9884-11e6-b146-0619f8945a4f");
            pnConfiguration.setPublishKey("pub-c-21663d8a-850d-4d99-adb3-3dda55a02abd");
            pnConfiguration.setSecure(false);

            PubNub pubnub = new PubNub(pnConfiguration);

            pubnub.addListener(new SubscribeCallback(){
                @Override
                public void status(PubNub pubnub, PNStatus status) {


                    if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                        // This event happens when radio / connectivity is lost
                    }

                    else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {

                        // Connect event. You can do stuff like publish, and know you'll get it.
                        // Or just use the connected event to confirm you are subscribed for
                        // UI / internal notifications, etc

                        if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
/*                            pubnub.publish().channel("awesomeChannel").message("hello!!").async(
                                    new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // Check whether request successfully completed or not.
                                    if (!status.isError()) {

                                        // Message successfully published to specified channel.
                                    }
                                    // Request processing failed.
                                    else {

                                        // Handle message publish error. Check 'category' property to find out possible issue
                                        // because of which request did fail.
                                        //
                                        // Request can be resent using: [status retry];
                                    }
                                }
                            });*/
                        }
                    }
                    else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                        // Happens as part of our regular operation. This event happens when
                        // radio / connectivity is lost, then regained.
                    }
                    else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                        // Handle messsage decryption error. Probably client configured to
                        // encrypt messages and on live data feed it received plain text.
                    }
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    // Handle new message stored in message.message
                    if (message.getChannel() != null) {
                        // Message has been received on channel group stored in
                        // message.getChannel()
                    }
                    else {
                        // Message has been received on channel stored in
                        // message.getSubscription()
                    }

            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()
            */
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
            String username = sharedPreferences.getString("usr","none");

            pubnub.subscribe()
                    .channels(Arrays.asList("6fecf37679")) // subscribe to channels
                    .execute();

        }
    }
}
