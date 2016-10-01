package com.ohereza.deliveryclerkmobileapp.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.ohereza.deliveryclerkmobileapp.R;

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;

/**
 * Created by rkabagamba on 9/30/2016.
 */

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        if (!usernameEditText.getText().toString().equalsIgnoreCase("") &&
                !passwordEditText.getText().toString().equalsIgnoreCase("")){

            // verify credentials to server


            // if( credential verified send registration token )



            String fcmInstanceId = sharedPreferences.getString("FCM_Token",null);
        }




        //sendToServer(login,)



    }


}
