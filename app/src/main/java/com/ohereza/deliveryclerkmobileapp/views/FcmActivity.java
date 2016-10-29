package com.ohereza.deliveryclerkmobileapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.helper.Configs;
import com.ohereza.deliveryclerkmobileapp.helper.FCMInstanceUpdate;
import com.ohereza.deliveryclerkmobileapp.helper.LoginResponse;
import com.ohereza.deliveryclerkmobileapp.interfaces.PdsAPI;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;

public class FcmActivity extends AppCompatActivity {
    private EditText retrievedEditText;
    private Button retrieveFCMToken;
    private Button sendToServerButton;
    private SharedPreferences sharedPreferences;
    private String refreshedToken="";
    private Context context;
    ClearableCookieJar cookieJar;
    OkHttpClient okHttpClient;
    Retrofit retrofit;
    PdsAPI pdsAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        retrievedEditText = (EditText) findViewById(R.id.retrieved_id_edit_text);
        retrieveFCMToken = (Button) findViewById(R.id.retrieve_token_button);
        sendToServerButton = (Button) findViewById(R.id.send_button);

        context = getApplicationContext();

        retrieveFCMToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();
                refreshedToken = sharedPreferences.getString("FCM_Token","none");
                // display id
                retrievedEditText.setText(refreshedToken);
                Toast.makeText(FcmActivity.this, refreshedToken, Toast.LENGTH_SHORT).show();
            }
        });

        sendToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                                 pdsAPI.updateFCMInstanceId(new FCMInstanceUpdate(
                                         sharedPreferences.getString("FCM_Token",null))).enqueue(
                                         new Callback<FCMInstanceUpdate>() {
                                             @Override
                                             public void onResponse(Call<FCMInstanceUpdate> call,
                                                                    Response<FCMInstanceUpdate> response) {
                                                 Toast.makeText(getApplicationContext(),"Posting successfull",
                                                         Toast.LENGTH_LONG).show();
                                             }

                                             @Override
                                             public void onFailure(Call<FCMInstanceUpdate> call, Throwable t) {
                                                 Toast.makeText(getApplicationContext(),"Posting unsuccessfull",
                                                         Toast.LENGTH_LONG).show();
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
    }
}
