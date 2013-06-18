package com.example.AndroidUITest.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkUtils {
    public static final String BACKEND_URL = "http://192.168.100.51:2403";

    public static HttpResponse sendRequest(HttpRequestBase request) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            Log.d("NetworkUtils.sendRequest", "Couldn't send request", e);
        }

        return response;
    }

    public static String getResponseAsString(HttpResponse response) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String l;
            while ((l = in.readLine()) != null) {
                result.append(l);
            }
            in.close();
        } catch (IOException e) {
            Log.d("NetworkUtils", "Couldn't read response", e);
        }
        return result.toString();
    }

    public static String encodeParams(String params) {
        String encode = null;
        try {
            encode = URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("NetworkUtils.encodeParams", "Couldn't read encode params", e);
        }
        return encode;
    }
}
