package com.ohereza.deliveryclerkmobileapp.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.ohereza.deliveryclerkmobileapp.R;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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

    private int sendToServer(String link, JSONObject jsonRequest){

        try {

            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(String.valueOf(jsonRequest));

            writer.close();

            return conn.getResponseCode();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
