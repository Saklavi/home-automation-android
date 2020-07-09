package com.wolty.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Utils {
    private SharedPreferences prefs;
    private HashMap<String, String> headers;

    Utils(Context mContext) {
        prefs = getDefaultSharedPreferences(mContext);
    }

    public Map<String, String> setHeaders(@Nullable String token) {
        headers = new HashMap<>();
        headers.put("accept", "application/json");

        if(token != null) {
            headers.put("Authorization", "Bearer " + token);
        }

        return headers;
    }

    public String getBaseUrl() {
        return prefs.getString("baseUrl", "http://192.168.254.118:81/api");
    }

    public void storeToken(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String retrieveToken() {
        return prefs.getString("token", "");
    }
}
