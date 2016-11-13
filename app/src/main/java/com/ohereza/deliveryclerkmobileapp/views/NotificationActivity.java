package com.ohereza.deliveryclerkmobileapp.views;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.helper.Configs;
import com.ohereza.deliveryclerkmobileapp.helper.DeliveryRequestUpdater;
import com.ohereza.deliveryclerkmobileapp.helper.DeliveryRequestUpdaterResponse;
import com.ohereza.deliveryclerkmobileapp.helper.LoginResponse;
import com.ohereza.deliveryclerkmobileapp.interfaces.PdsAPI;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends AppCompatActivity {

    private Button acceptButton;
    private Button rejectButton;
    private ClearableCookieJar cookieJar;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private PdsAPI pdsAPI;
    private Location mCurrentLocation;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        acceptButton = (Button) findViewById(R.id.accept_button);
        rejectButton = (Button) findViewById(R.id.reject_button);

        // Vibrator
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long pattern[] = {60,120,180,240,300,360,420,480};
        vibrator.vibrate(pattern, 1);



        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.cancel();
                cookieJar = new PersistentCookieJar(new SetCookieCache(),
                        new SharedPrefsCookiePersistor(getApplicationContext()));
                okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(Configs.serverAddress)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

                pdsAPI = retrofit.create(PdsAPI.class);

                pdsAPI.login("Administrator", "pds").enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response){

                        pdsAPI.updateDeliveryRequest( "c36606233b", new DeliveryRequestUpdater("Assigned")).enqueue(
                                new Callback<DeliveryRequestUpdaterResponse>() {
                                    @Override
                                    public void onResponse(Call<DeliveryRequestUpdaterResponse> call,
                                                           Response<DeliveryRequestUpdaterResponse> response){
                                        Toast.makeText(getApplicationContext(),
                                                "Request successfully accepted",
                                                Toast.LENGTH_LONG).show();
                                        finish();

                                    }

                                    @Override
                                    public void onFailure(Call<DeliveryRequestUpdaterResponse> call, Throwable t){
                                    }
                                }
                        );
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                    }
                });
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.cancel();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
