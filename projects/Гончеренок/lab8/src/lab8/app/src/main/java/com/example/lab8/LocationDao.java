package com.example.lab8;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    void insert(LocationPoint location);

    @Update
    void update(LocationPoint location);

    @Delete
    void delete(LocationPoint location);

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    List<LocationPoint> getAllLocations();

    @Query("SELECT * FROM locations WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    List<LocationPoint> getLocationsBetween(long startTime, long endTime);

    @Query("SELECT * FROM locations WHERE timestamp >= :since ORDER BY timestamp DESC")
    List<LocationPoint> getRecentLocations(long since);

    @Query("SELECT COUNT(*) FROM locations WHERE timestamp >= :since")
    int getCountSince(long since);

    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    LocationPoint getLastLocation();
}