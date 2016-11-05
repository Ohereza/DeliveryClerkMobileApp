package com.ohereza.deliveryclerkmobileapp.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;

/**
 * Created by rkabagamba on 10/1/2016.
 */

public class SplashScreenActivity extends AppCompatActivity{
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (sharedPreferences.getString("usr","").equalsIgnoreCase("")
                || sharedPreferences.getString("pwd","").equalsIgnoreCase("")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    public void onStop(){
        super.onStop();
        finish();
    }
}
