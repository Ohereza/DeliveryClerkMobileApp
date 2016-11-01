package com.ohereza.deliveryclerkmobileapp.views;

import android.content.Intent;
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
import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.helper.Configs;
import com.ohereza.deliveryclerkmobileapp.helper.LoginResponse;
import com.ohereza.deliveryclerkmobileapp.helper.ServerConnector;
import com.ohereza.deliveryclerkmobileapp.interfaces.PdsAPI;
import com.ohereza.deliveryclerkmobileapp.services.LocatorService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;

/**
 * Created by rkabagamba on 9/30/2016.
 */

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ServerConnector mConnector;
    private Button submitButton;
    private String username;
    private String password;
    ClearableCookieJar cookieJar;
    OkHttpClient okHttpClient;
    Retrofit retrofit;
    PdsAPI pdsAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        mConnector = new ServerConnector();

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        submitButton = (Button) findViewById(R.id.submit_button);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!usernameEditText.getText().toString().equalsIgnoreCase("") &&
                        !passwordEditText.getText().toString().equalsIgnoreCase("")){

                    username = usernameEditText.getText().toString().trim();
                    password = passwordEditText.getText().toString().trim();

                    // connect to server
                    pdsAPI.login(username, password).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call,
                                               Response<LoginResponse> response) {
                            if (response.code() == 200){
                                // store credentials
                                sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("usr",username);
                                editor.putString("pwd",password);
                                editor.commit();

                                /*System.out.println("fcm instanceid: " +
                                        sharedPreferences.getString("FCM_Token",null));
                                // retrieve FCM instance id and update server
                                if (sharedPreferences.getString("FCM_Token",null) != null) {
                                    pdsAPI.updateFirebaseInstanceId(
                                            sharedPreferences.getString("FCM_Token",null)
                                                ).enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call,
                                                                           Response<Void> response){}

                                                    @Override
                                                    public void onFailure(Call<Void> call,
                                                                          Throwable t) {}
                                                          });

                                            mConnector.updateFirebaseInstanceId(username,
                                                    sharedPreferences.getString("FCM_Token", null));
                                }*/

                                // start locator service
                                Intent locationServiceIntent = new Intent(LoginActivity.this,
                                                                            LocatorService.class);
                                startService(locationServiceIntent);

                                //launch main activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        R.string.login_error_msg,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.login_error_msg,Toast.LENGTH_LONG).show();
                        }
                    });


                    String fcmInstanceId = sharedPreferences.getString("FCM_Token",null);

                }else{
                    Toast.makeText(getApplicationContext(),
                            R.string.missing_information_msg,Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }
}
