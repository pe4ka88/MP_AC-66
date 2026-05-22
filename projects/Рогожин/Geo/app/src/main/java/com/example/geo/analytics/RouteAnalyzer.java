package com.example.geo.analytics;

import android.content.Context;
import android.location.Location;

import com.example.geo.data.LocationPoint;
import com.example.geo.utils.GeocoderHelper;

import java.util.ArrayList;
import java.util.List;

public class RouteAnalyzer {

    private static final float STOP_RADIUS = 30f;      // метров
    private static final long STOP_TIME = 5 * 60 * 1000; // 5 минут
    private static final float WALK_SPEED = 2.0f;      // м/с

    /**
     * Анализ маршрута с определением сегментов и геокодированием конца каждого сегмента.
     * @param context контекст для GeocoderHelper
     * @param points список GPS точек
     * @return список сегментов движения
     */
    public static List<MovementSegment> analyze(Context context, List<LocationPoint> points) {

        List<MovementSegment> result = new ArrayList<>();

        if (points.size() < 2) return result;

        MovementSegment current = null;

        for (int i = 1; i < points.size(); i++) {

            LocationPoint a = points.get(i - 1);
            LocationPoint b = points.get(i);

            Location la = new Location("");
            la.setLatitude(a.latitude);
            la.setLongitude(a.longitude);

            Location lb = new Location("");
            lb.setLatitude(b.latitude);
            lb.setLongitude(b.longitude);

            float dist = la.distanceTo(lb);
            long dt = b.timestamp - a.timestamp;
            float speed = dist / (dt / 1000f);

            MovementSegment.Type type;

            if (dist < STOP_RADIUS && dt > STOP_TIME) {
                type = MovementSegment.Type.STOP;
            } else if (speed < WALK_SPEED) {
                type = MovementSegment.Type.WALK;
            } else {
                type = MovementSegment.Type.CAR;
            }

            // Новый сегмент
            if (current == null || current.type != type) {

                current = new MovementSegment(type);
                current.startTime = a.timestamp;
                current.latitude = a.latitude;
                current.longitude = a.longitude;

                result.add(current);
            }

            // Обновляем конец сегмента
            current.endTime = b.timestamp;
            current.endLatitude = b.latitude;
            current.endLongitude = b.longitude;
            current.distance += dist;
        }

        // Объединяем мелкие сегменты
        List<MovementSegment> merged = mergeSmallSegments(result);

        // Геокодирование: конец каждого сегмента
        for (MovementSegment s : merged) {
            double lat = s.getCenterLat();
            double lon = s.getCenterLon();

            // Безопасное присвоение через setter
            s.setPlace(GeocoderHelper.getPlace(context, lat, lon));
        }

        return merged;
    }

    private static List<MovementSegment> mergeSmallSegments(List<MovementSegment> list) {

        List<MovementSegment> merged = new ArrayList<>();

        for (MovementSegment s : list) {

            if (!merged.isEmpty()) {

                MovementSegment prev = merged.get(merged.size() - 1);

                if (prev.type == s.type) {

                    prev.endTime = s.endTime;
                    prev.endLatitude = s.endLatitude;
                    prev.endLongitude = s.endLongitude;
                    prev.distance += s.distance;

                    continue;
                }
            }

            merged.add(s);
        }

        return merged;
    }
}