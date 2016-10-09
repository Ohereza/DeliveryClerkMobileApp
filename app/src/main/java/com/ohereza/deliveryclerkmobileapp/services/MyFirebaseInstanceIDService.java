package com.ohereza.deliveryclerkmobileapp.services;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;

/**
 * Created by rkabagamba on 9/28/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private SharedPreferences sharedPreferences;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("registration token: "+refreshedToken);

        // store registration token
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FCM_Token",refreshedToken);
        editor.commit();

        // send token to server
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

    }
}
