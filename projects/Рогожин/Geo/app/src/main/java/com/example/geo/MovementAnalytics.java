package com.example.geo;

import android.location.Location;

import com.example.geo.model.TrackPoint;

import java.util.List;

public class MovementAnalytics {

    public static class Result {
        public long totalTime;
        public long walkTime;
        public long bikeTime;
        public long carTime;
    }

    private static float distanceMeters(TrackPoint p1, TrackPoint p2) {
        float[] results = new float[1];
        Location.distanceBetween(
                p1.lat, p1.lon,
                p2.lat, p2.lon,
                results
        );
        return results[0];
    }

    public static Result analyze(List<TrackPoint> points) {

        Result result = new Result();

        if (points.size() < 2) return result;

        for (int i = 0; i < points.size() - 1; i++) {

            TrackPoint p1 = points.get(i);
            TrackPoint p2 = points.get(i + 1);

            float distance = distanceMeters(p1, p2);
            double timeSec = (p2.timestamp - p1.timestamp) / 1000.0;

            if (timeSec <= 0) continue;

            double speedKmh = (distance / timeSec) * 3.6;
            long segmentTime = p2.timestamp - p1.timestamp;

            if (speedKmh <= 6) {
                result.walkTime += segmentTime;
            } else if (speedKmh <= 20) {
                result.bikeTime += segmentTime;
            } else {
                result.carTime += segmentTime;
            }
        }

        result.totalTime = result.walkTime +
                result.bikeTime +
                result.carTime;

        return result;
    }
}
