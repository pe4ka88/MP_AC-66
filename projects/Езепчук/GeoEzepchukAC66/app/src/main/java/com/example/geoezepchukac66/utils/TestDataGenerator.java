package com.example.geoezepchukac66.utils;

import android.util.Log;
import com.example.geoezepchukac66.data.LocationDatabaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Генератор тестовых данных студента БрГТУ на неделю.
 * Оптимизирован: меньше точек, маршруты немного различаются.
 */
public class TestDataGenerator {

    private static final String TAG = "TestDataGenerator";
    private static final long MINUTE = 60 * 1000L;
    private static final long HOUR = 60 * MINUTE;
    private static final Random random = new Random();

    private static final double DORM_LAT = 52.09650, DORM_LON = 23.76250;
    private static final double UNI_LAT = 52.09670, UNI_LON = 23.75890;
    private static final double KFC_LAT = 52.09350, KFC_LON = 23.75850;
    private static final double MALL_LAT = 52.09685, MALL_LON = 23.74493;
    private static final double PARK_LAT = 52.09580, PARK_LON = 23.70060;

    public static void generate(LocationDatabaseHelper db) {
        Log.d(TAG, "Очистка базы и генерация маршрутов на неделю...");

        db.clearAll();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6); // старт недели (6 дней назад)
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);

        // Генерация маршрута на каждый день
        for (int day = 0; day < 7; day++) {
            long currentTime = cal.getTimeInMillis();
            Log.d(TAG, "Генерация маршрута для дня: " + day);

            // Сборы в общежитии
            currentTime = addStop(db, jitter(DORM_LAT), jitter(DORM_LON), currentTime, 40 * MINUTE);

            // Путь до университета
            currentTime = createRouteOSM(db, jitter(DORM_LAT), jitter(DORM_LON),
                    jitter(UNI_LAT), jitter(UNI_LON), currentTime, 4);

            // Учеба
            currentTime = addStop(db, jitter(UNI_LAT), jitter(UNI_LON), currentTime, 4 * HOUR);

            // Обед в KFC
            currentTime = createRouteOSM(db, jitter(UNI_LAT), jitter(UNI_LON),
                    jitter(KFC_LAT), jitter(KFC_LON), currentTime, 5);
            currentTime = addStop(db, jitter(KFC_LAT), jitter(KFC_LON), currentTime, 50 * MINUTE);

            // Поход в ТЦ Корона
            currentTime = createRouteOSM(db, jitter(KFC_LAT), jitter(KFC_LON),
                    jitter(MALL_LAT), jitter(MALL_LON), currentTime, 10);
            currentTime = randomMovement(db, jitter(MALL_LAT), jitter(MALL_LON), currentTime, 1 * HOUR);

            // Вечерняя прогулка в Парк
            currentTime = createRouteOSM(db, jitter(MALL_LAT), jitter(MALL_LON),
                    jitter(PARK_LAT), jitter(PARK_LON), currentTime, 15);
            currentTime = randomMovement(db, jitter(PARK_LAT), jitter(PARK_LON), currentTime, 2 * HOUR);

            // Возвращение в общежитие
            currentTime = createRouteOSM(db, jitter(PARK_LAT), jitter(PARK_LON),
                    jitter(DORM_LAT), jitter(DORM_LON), currentTime, 5);
            addStop(db, jitter(DORM_LAT), jitter(DORM_LON), currentTime, 3 * HOUR);

            // Переходим на следующий день
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Log.d(TAG, "Генерация недели завершена.");
    }

    // ------------------------------
    // Создаем маршрут через OSRM (оптимизированно)
    // ------------------------------
    private static long createRouteOSM(LocationDatabaseHelper db,
                                       double lat1, double lon1,
                                       double lat2, double lon2,
                                       long startTime, double speedKmh) {
        try {
            String urlStr = String.format(Locale.US,
                    "https://router.project-osrm.org/route/v1/foot/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                    lon1, lat1, lon2, lat2);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return startTime;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray routes = json.getJSONArray("routes");
            if (routes.length() == 0) return startTime;

            JSONArray coords = routes.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");

            double speedMs = speedKmh / 3.6;
            long time = startTime;
            long lastInsertTime = 0;

            for (int i = 0; i < coords.length(); i++) {
                JSONArray pt = coords.getJSONArray(i);
                double lon = pt.getDouble(0);
                double lat = pt.getDouble(1);

                if ((time - lastInsertTime) >= 45000 || i == coords.length() - 1) {
                    db.insert(lat, lon, time);
                    lastInsertTime = time;
                }

                if (i < coords.length() - 1) {
                    JSONArray nextPt = coords.getJSONArray(i + 1);
                    double nextLon = nextPt.getDouble(0);
                    double nextLat = nextPt.getDouble(1);
                    double dist = distanceMeters(lat, lon, nextLat, nextLon);
                    time += (long) ((dist / speedMs) * 1000);
                }
            }

            return time;
        } catch (Exception e) {
            Log.e(TAG, "OSRM routing failed", e);
            return startTime;
        }
    }

    // ------------------------------
    // STOP
    // ------------------------------
    private static long addStop(LocationDatabaseHelper db, double lat, double lon, long startTime, long duration) {
        long interval = 2 * MINUTE;
        long time = startTime;
        long end = startTime + duration;
        while (time < end) {
            db.insert(lat + (random.nextDouble() - 0.5) * 0.00005,
                    lon + (random.nextDouble() - 0.5) * 0.00005, time);
            time += interval;
        }
        return end;
    }

    // ------------------------------
    // Random movement
    // ------------------------------
    private static long randomMovement(LocationDatabaseHelper db, double lat, double lon, long startTime, long duration) {
        long interval = 60 * 1000;
        long time = startTime;
        long end = startTime + duration;
        double currLat = lat, currLon = lon;
        while (time < end) {
            currLat += (random.nextDouble() - 0.5) * 0.0002;
            currLon += (random.nextDouble() - 0.5) * 0.0002;
            if (distanceMeters(lat, lon, currLat, currLon) > 80) {
                currLat = lat; currLon = lon;
            }
            db.insert(currLat, currLon, time);
            time += interval;
        }
        return end;
    }

    // ------------------------------
    // Методы утилиты
    // ------------------------------
    private static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    // ------------------------------
    // Добавляем случайное смещение для разнообразия маршрутов
    // ------------------------------
    private static double jitter(double value) {
        return value + (random.nextDouble() - 0.5) * 0.0003;
    }
}