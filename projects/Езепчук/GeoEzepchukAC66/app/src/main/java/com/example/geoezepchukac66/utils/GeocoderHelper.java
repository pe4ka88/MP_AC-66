package com.example.geoezepchukac66.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class GeocoderHelper {

    private static final String TAG = "GeocoderHelper";

    // Кэш максимум 200 мест
    private static final Map<String, String> cache =
            new LinkedHashMap<String, String>(200, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > 200;
                }
            };

    // Ограничение Nominatim
    private static long lastRequestTime = 0;
    private static final long REQUEST_DELAY = 1200;

    public static String getPlace(Context context, double lat, double lon) {

        // округление до ~110 метров
        String key = String.format(Locale.US, "%.3f_%.3f", lat, lon);

        if (cache.containsKey(key)) {
            Log.d(TAG, "Cache HIT: " + key);
            return cache.get(key);
        }

        try {

            rateLimit();

            Log.d(TAG, "Cache MISS -> network request: " + lat + "," + lon);

            String place = reverse(lat, lon);

            cache.put(key, place);

            return place;

        } catch (Exception e) {

            Log.e(TAG, "Geocoder error at [" + lat + "," + lon + "]", e);

            return "Unknown place";
        }
    }

    private static synchronized void rateLimit() {

        long now = System.currentTimeMillis();
        long diff = now - lastRequestTime;

        if (diff < REQUEST_DELAY) {

            try {
                Thread.sleep(REQUEST_DELAY - diff);
            } catch (InterruptedException ignored) {}

        }

        lastRequestTime = System.currentTimeMillis();
    }

    private static String reverse(double lat, double lon) {

        try {

            String urlStr = String.format(Locale.US,
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.6f&lon=%.6f&addressdetails=1&namedetails=1&accept-language=ru",
                    lat, lon);

            Log.i(TAG, "Request: " + urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setConnectTimeout(7000);
            conn.setReadTimeout(7000);

            conn.setRequestProperty("User-Agent", "GeoEzepchukAC66 Android App");
            conn.setRequestProperty("Accept-Language", "ru");

            int responseCode = conn.getResponseCode();

            // защита от rate limit
            if (responseCode == 429) {

                Log.w(TAG, "Rate limit reached. Waiting 3 seconds...");

                Thread.sleep(3000);

                return reverse(lat, lon);
            }

            if (responseCode != 200) {

                Log.e(TAG, "Server returned error code: " + responseCode);

                return "Location Error (" + responseCode + ")";
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            JSONObject address = json.optJSONObject("address");

            if (address == null) {
                return "Unknown Address";
            }

            String name = "";

            JSONObject nameDetails = json.optJSONObject("namedetails");

            if (nameDetails != null) {
                name = nameDetails.optString("name", "");
            }

            String road = address.optString("road", "");
            String house = address.optString("house_number", "");

            StringBuilder result = new StringBuilder();

            if (!name.isEmpty()) result.append(name);

            if (!road.isEmpty()) {
                if (result.length() > 0) result.append(", ");
                result.append(road);
            }

            if (!house.isEmpty()) {
                result.append(", ").append(house);
            }

            if (result.length() == 0) {
                result.append(json.optString("display_name", "Unknown Location"));
            }

            String finalResult = result.toString();

            Log.i(TAG, "Resolved address: " + finalResult);

            return finalResult;

        } catch (Exception e) {

            Log.e(TAG, "Geocoding exception", e);

            return "Geocoding failed";
        }
    }
}