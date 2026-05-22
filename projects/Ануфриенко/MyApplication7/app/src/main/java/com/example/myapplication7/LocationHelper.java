package com.example.myapplication7;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

/**
 * LocationHelper — получение координат устройства и расчёт расстояний.
 */
public class LocationHelper {

    private final Context         context;
    private final LocationManager locationManager;
    private       Location        lastLocation;

    /** Интерфейс обратного вызова при обновлении координат. */
    public interface OnLocationUpdateListener {
        void onUpdate(Location location);
    }

    public LocationHelper(Context context) {
        this.context         = context;
        this.locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
    }

    /** Запросить последнее известное местоположение (без подписки). */
    public Location getLastKnownLocation() {
        if (!hasPermission()) return null;
        try {
            Location gps = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location net = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps != null && net != null)
                return gps.getAccuracy() <= net.getAccuracy() ? gps : net;
            if (gps != null) return gps;
            return net;
        } catch (SecurityException e) {
            return null;
        }
    }

    /** Подписаться на обновления координат. Вызывать из onResume(). */
    public void startUpdates(OnLocationUpdateListener listener) {
        if (!hasPermission()) return;
        LocationListener ll = new LocationListener() {
            @Override public void onLocationChanged(Location loc) {
                lastLocation = loc;
                if (listener != null) listener.onUpdate(loc);
            }
            @Override public void onStatusChanged(String p, int s, Bundle b) {}
            @Override public void onProviderEnabled(String p) {}
            @Override public void onProviderDisabled(String p) {}
        };
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 5, ll);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 5000, 5, ll);
        } catch (SecurityException e) { /* нет разрешения */ }
    }

    /**
     * Расстояние между двумя точками в метрах.
     * Использует формулу Хаверсина через Location.distanceBetween().
     */
    public static float distanceBetween(double lat1, double lon1,
                                        double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    /**
     * Форматированная строка координат для отображения пользователю.
     */
    public String getLocationString() {
        Location loc = getLastKnownLocation();
        if (loc != null)
            return String.format("%.5f°N  %.5f°E", loc.getLatitude(), loc.getLongitude());
        return "Координаты недоступны";
    }

    public double getLatitude() {
        Location loc = getLastKnownLocation();
        return loc != null ? loc.getLatitude() : 0.0;
    }

    public double getLongitude() {
        Location loc = getLastKnownLocation();
        return loc != null ? loc.getLongitude() : 0.0;
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}