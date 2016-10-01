package com.ohereza.deliveryclerkmobileapp.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.helper.ServerConnector;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

                    String username = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    int responseCode = mConnector.loginToServer(username, password);

                    System.out.println("returned response: "+responseCode);

                    if (responseCode == 200){
                        // store credentials
                        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("usr",username);
                        editor.putString("pwd",password);
                        editor.commit();
                        //launch main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.login_error_msg,Toast.LENGTH_LONG).show();
                    }

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
