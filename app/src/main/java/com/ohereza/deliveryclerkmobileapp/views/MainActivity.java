package com.ohereza.deliveryclerkmobileapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ohereza.deliveryclerkmobileapp.R;

import org.json.JSONException;
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

public class MainActivity extends AppCompatActivity {
    private EditText retrievedEditText;
    private Button retrieveFCMToken;
    private Button sendToServerButton;
    private SharedPreferences sharedPreferences;
    private String refreshedToken="";
    private Context context;

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
                Toast.makeText(MainActivity.this, refreshedToken, Toast.LENGTH_SHORT).show();
            }
        });

        sendToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonRequest = new JSONObject();
                try {
                    String link = "";
                    refreshedToken = sharedPreferences.getString("FCM_Token",null);
                    jsonRequest.put("Token", refreshedToken);

                    // send to server
                    sendToServer(link,jsonRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
