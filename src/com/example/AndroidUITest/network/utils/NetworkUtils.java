package com.example.AndroidUITest.network.utils;

import android.util.Log;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkUtils {

    public static JSONArray parseAsJsonArray(HttpResponse response) {
        JSONArray jsonArray = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String l;
            while ((l = in.readLine()) != null) {
                result.append(l);
            }
            in.close();
            jsonArray = new JSONArray(result.toString());
        } catch (JSONException e) {
            Log.d("NetworkUtils.parseAsJsonArray", "Couldn't parse JSON", e);
        } catch (IOException e) {
            Log.d("NetworkUtils.parseAsJsonArray", "Couldn't read response", e);
        }
        return jsonArray;
    }

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
