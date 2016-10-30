package com.ohereza.deliveryclerkmobileapp.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.interfaces.PdsAPI;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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

        }
    }
}
