package com.ohereza.deliveryclerkmobileapp.helper;

import android.os.StrictMode;

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

import static com.ohereza.deliveryclerkmobileapp.helper.Configs.loginRelatedUri;
import static com.ohereza.deliveryclerkmobileapp.helper.Configs.serverAddress;
import static com.ohereza.deliveryclerkmobileapp.helper.Configs.updateFcmInstanceIdUri;

/**
 * Created by rkabagamba on 10/1/2016.
 */


public class ServerConnector {

    public int loginToServer(String username, String password) {
        String request = "usr=" + username + "&pwd=" + password;
        return postXWFUFormatToServer(request, loginRelatedUri);
    }

    public void updateFirebaseInstanceId(String username, String instanceId){
        System.out.println("send instance id");
        String uri = updateFcmInstanceIdUri+"/"+username;
        String request = "data={\"fcm_instance_id\":\""+instanceId +"\"}";

/*        JSONObject request = new JSONObject();
        try {
            request.put("fcm_instance_id",instanceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        System.out.println(request);
        putXWFUFormatToServer(request,uri);

    }


    public void signUp(String username, String password){

    }





    public int postJsonFormatToServer(JSONObject jsonRequest, String partialUri){

        try {

            URL url = new URL(serverAddress+partialUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

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

    public int postXWFUFormatToServer(String request, String partialUri){

        try {

            System.out.println(serverAddress + partialUri);
            URL url = new URL(serverAddress + partialUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(String.valueOf(request));

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


    public int putXWFUFormatToServer(String request, String partialUri){

        try {
            System.out.println(serverAddress+partialUri);
            URL url = new URL(serverAddress+partialUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("PUT");
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(String.valueOf(request));

            writer.close();
            System.out.println("response code: "+conn.getResponseCode());
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
