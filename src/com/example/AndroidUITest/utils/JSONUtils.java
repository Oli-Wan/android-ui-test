package com.example.AndroidUITest.utils;

import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONUtils {

    public static <T> T decode(String json, Class<T> type) {
        if (json == null || json.isEmpty()) {
            Log.d("JSONUtils.decode", "Null or empty string, nothing to do");
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        T result = null;
        try {
            result = mapper.readValue(json, type);
        } catch (IOException e) {
            Log.e("JSONUtils.decode", "Couldn't decode data : " + json, e);
        }
        return result;
    }

    public static String encode(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            Log.e("JSONUtils.encode", "Couldn't encode object : " + data.toString(), e);
        }
        return result;
    }
}
