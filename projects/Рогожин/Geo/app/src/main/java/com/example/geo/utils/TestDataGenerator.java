package com.example.geo.utils;

import android.location.Location;
import android.util.Log;

import com.example.geo.data.LocationDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class TestDataGenerator {

    private static final String TAG = "TestDataGenerator";

    private static final long MINUTE = 60 * 1000L;
    private static final long HOUR = 60 * MINUTE;

    private static final Random random = new Random();

    // Дом (Гоголя 87А)
    private static final double HOME_LAT = 52.09745;
    private static final double HOME_LON = 23.76210;

    // БрГТУ
    private static final double UNI_LAT = 52.09670;
    private static final double UNI_LON = 23.75890;

    // KFC
    private static final double KFC_LAT = 52.09350;
    private static final double KFC_LON = 23.75850;

    // ТЦ Корона
    private static final double MALL_LAT = 52.09685;
    private static final double MALL_LON = 23.74493;

    // Парк
    private static final double PARK_LAT = 52.09580;
    private static final double PARK_LON = 23.70060;

    // Магазин Санта (Гоголя 85В)
    private static final double SANTA_LAT = 52.09710;
    private static final double SANTA_LON = 23.76160;

    public static void generate(LocationDatabaseHelper db) {

        Log.d(TAG, "Очистка базы...");
        db.clearAll();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);

        for (int day = 0; day < 7; day++) {

            long time = cal.getTimeInMillis();

            Log.d(TAG, "Генерация дня: " + day);

            // утро дома
            time = addStop(db, jitter(HOME_LAT), jitter(HOME_LON), time, 40 * MINUTE);

            int variant = random.nextInt(3);

            if (variant == 0) {

                // дом → университет
                time = createRouteOSM(db, HOME_LAT, HOME_LON, UNI_LAT, UNI_LON, time);

                // учеба
                time = addStop(db, UNI_LAT, UNI_LON, time, 4 * HOUR);

                // универ → KFC
                time = createRouteOSM(db, UNI_LAT, UNI_LON, KFC_LAT, KFC_LON, time);
                time = addStop(db, KFC_LAT, KFC_LON, time, 40 * MINUTE);

                // KFC → ТЦ
                time = createRouteOSM(db, KFC_LAT, KFC_LON, MALL_LAT, MALL_LON, time);
                time = randomMovement(db, MALL_LAT, MALL_LON, time, 1 * HOUR);

                // ТЦ → парк
                time = createRouteOSM(db, MALL_LAT, MALL_LON, PARK_LAT, PARK_LON, time);
                time = randomMovement(db, PARK_LAT, PARK_LON, time, 1 * HOUR);

            } else if (variant == 1) {

                // дом → университет
                time = createRouteOSM(db, HOME_LAT, HOME_LON, UNI_LAT, UNI_LON, time);
                time = addStop(db, UNI_LAT, UNI_LON, time, 3 * HOUR);

                // университет → Санта
                time = createRouteOSM(db, UNI_LAT, UNI_LON, SANTA_LAT, SANTA_LON, time);
                time = addStop(db, SANTA_LAT, SANTA_LON, time, 20 * MINUTE);

                // Санта → парк
                time = createRouteOSM(db, SANTA_LAT, SANTA_LON, PARK_LAT, PARK_LON, time);
                time = randomMovement(db, PARK_LAT, PARK_LON, time, 2 * HOUR);

            } else {

                // дом → ТЦ
                time = createRouteOSM(db, HOME_LAT, HOME_LON, MALL_LAT, MALL_LON, time);
                time = randomMovement(db, MALL_LAT, MALL_LON, time, 2 * HOUR);

                // ТЦ → университет
                time = createRouteOSM(db, MALL_LAT, MALL_LON, UNI_LAT, UNI_LON, time);
                time = addStop(db, UNI_LAT, UNI_LON, time, 2 * HOUR);

                // университет → KFC
                time = createRouteOSM(db, UNI_LAT, UNI_LON, KFC_LAT, KFC_LON, time);
                time = addStop(db, KFC_LAT, KFC_LON, time, 30 * MINUTE);
            }

            // возвращение домой
            time = createRouteOSM(db, PARK_LAT, PARK_LON, HOME_LAT, HOME_LON, time);
            addStop(db, HOME_LAT, HOME_LON, time, 3 * HOUR);

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Log.d(TAG, "Генерация завершена");
    }

    // ===============================
    // OSRM маршрут
    // ===============================

    private static long createRouteOSM(LocationDatabaseHelper db,
                                       double lat1, double lon1,
                                       double lat2, double lon2,
                                       long startTime) {

        try {

            String urlStr = String.format(Locale.US,
                    "https://router.project-osrm.org/route/v1/foot/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                    lon1, lat1, lon2, lat2);

            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200)
                return startTime;

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line);

            reader.close();

            JSONObject json = new JSONObject(sb.toString());

            JSONArray coords = json
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");

            double dist = distanceMeters(lat1, lon1, lat2, lon2);
            double speed = chooseSpeed(dist);
            double speedMs = speed / 3.6;

            long time = startTime;
            long lastInsert = 0;

            for (int i = 0; i < coords.length(); i++) {

                JSONArray p = coords.getJSONArray(i);

                double lon = p.getDouble(0);
                double lat = p.getDouble(1);

                if (time - lastInsert > 45000 || i == coords.length() - 1) {
                    db.insert(lat, lon, time);
                    lastInsert = time;
                }

                if (i < coords.length() - 1) {

                    JSONArray next = coords.getJSONArray(i + 1);

                    double nLon = next.getDouble(0);
                    double nLat = next.getDouble(1);

                    double d = distanceMeters(lat, lon, nLat, nLon);

                    time += (long) ((d / speedMs) * 1000);
                }
            }

            return time;

        } catch (Exception e) {

            Log.e(TAG, "OSRM error", e);
            return startTime;
        }
    }

    // ===============================
    // выбор скорости (пешком/вел/транспорт)
    // ===============================

    private static double chooseSpeed(double distance) {

        if (distance > 2000)
            return 35 + random.nextInt(20); // транспорт

        if (distance > 800)
            return 15 + random.nextInt(10); // велосипед

        return 4 + random.nextDouble() * 2; // пешком
    }

    // ===============================
    // остановка
    // ===============================

    private static long addStop(LocationDatabaseHelper db,
                                double lat, double lon,
                                long start,
                                long duration) {

        long time = start;
        long end = start + duration;

        while (time < end) {

            db.insert(
                    lat + (random.nextDouble() - 0.5) * 0.00005,
                    lon + (random.nextDouble() - 0.5) * 0.00005,
                    time
            );

            time += 2 * MINUTE;
        }

        return end;
    }

    // ===============================
    // случайное движение
    // ===============================

    private static long randomMovement(LocationDatabaseHelper db,
                                       double lat,
                                       double lon,
                                       long start,
                                       long duration) {

        long time = start;
        long end = start + duration;

        double curLat = lat;
        double curLon = lon;

        while (time < end) {

            curLat += (random.nextDouble() - 0.5) * 0.0002;
            curLon += (random.nextDouble() - 0.5) * 0.0002;

            if (distanceMeters(lat, lon, curLat, curLon) > 80) {
                curLat = lat;
                curLon = lon;
            }

            db.insert(curLat, curLon, time);

            time += MINUTE;
        }

        return end;
    }

    // ===============================
    // расстояние
    // ===============================

    private static double distanceMeters(double lat1, double lon1,
                                         double lat2, double lon2) {

        float[] res = new float[1];

        Location.distanceBetween(
                lat1, lon1,
                lat2, lon2,
                res
        );

        return res[0];
    }

    // ===============================
    // jitter GPS
    // ===============================

    private static double jitter(double value) {

        return value + (random.nextDouble() - 0.5) * 0.0003;
    }
}