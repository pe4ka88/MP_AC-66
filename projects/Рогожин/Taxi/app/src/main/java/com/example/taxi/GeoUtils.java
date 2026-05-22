package com.example.taxi;

import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeoUtils {

    private static final String TAG = "GeoUtils";

    public interface GeocodeCallback {
        void onResult(String address);
        void onError(String error);
    }

    public static void reverseGeocode(double lat, double lon, GeocodeCallback callback) {
        new Thread(() -> {
            try {
                String urlStr = String.format(
                        "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&addressdetails=1",
                        lat, lon
                );

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(7000);
                conn.setReadTimeout(7000);
                conn.setRequestProperty("User-Agent", "Taxi/1.0 (2peacewxrld@gmail.com)");
                conn.connect();

                InputStream is = conn.getInputStream();
                Scanner sc = new Scanner(is);
                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) sb.append(sc.nextLine());
                sc.close();
                is.close();

                JSONObject json = new JSONObject(sb.toString());
                String displayName = json.optString("display_name", lat + ", " + lon);

                callback.onResult(displayName);

            } catch (Exception e) {
                Log.e(TAG, "Reverse geocode error", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
}