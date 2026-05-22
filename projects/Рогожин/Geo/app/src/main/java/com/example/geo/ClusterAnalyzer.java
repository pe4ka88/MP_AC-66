package com.example.geo;

import android.location.Location;

import com.example.geo.model.Cluster;
import com.example.geo.model.TrackPoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class ClusterAnalyzer {

    private static final double EPS = 50; // радиус в метрах
    private static final int MIN_POINTS = 5;

    public static List<Cluster> cluster(List<TrackPoint> points) {

        List<Cluster> clusters = new ArrayList<>();
        Set<TrackPoint> visited = new HashSet<>();

        for (TrackPoint point : points) {

            if (visited.contains(point)) continue;

            visited.add(point);

            List<TrackPoint> neighbors = getNeighbors(point, points);

            if (neighbors.size() >= MIN_POINTS) {

                Cluster cluster = new Cluster();
                expandCluster(cluster, point, neighbors, points, visited);
                clusters.add(cluster);
            }
        }

        return clusters;
    }

    private static void expandCluster(
            Cluster cluster,
            TrackPoint point,
            List<TrackPoint> neighbors,
            List<TrackPoint> points,
            Set<TrackPoint> visited
    ) {

        cluster.points.add(point);

        Queue<TrackPoint> queue = new LinkedList<>(neighbors);

        while (!queue.isEmpty()) {

            TrackPoint current = queue.poll();

            if (!visited.contains(current)) {
                visited.add(current);

                List<TrackPoint> currentNeighbors =
                        getNeighbors(current, points);

                if (currentNeighbors.size() >= MIN_POINTS) {
                    queue.addAll(currentNeighbors);
                }
            }

            if (!cluster.points.contains(current)) {
                cluster.points.add(current);
            }
        }
    }

    private static List<TrackPoint> getNeighbors(
            TrackPoint center,
            List<TrackPoint> points
    ) {

        List<TrackPoint> neighbors = new ArrayList<>();

        for (TrackPoint point : points) {
            if (distanceMeters(center, point) <= EPS) {
                neighbors.add(point);
            }
        }

        return neighbors;
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
}
