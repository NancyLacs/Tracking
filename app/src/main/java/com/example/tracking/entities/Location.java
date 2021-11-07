package com.example.tracking.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(
        entity = Trip.class,
        parentColumns = "tripId",
        childColumns = "fk_trip",
        onDelete = ForeignKey.CASCADE
)})

public class Location {
    @PrimaryKey(autoGenerate = true)
    public long locationId;
    public long fk_trip;
    public int partOfTrip; //0=start, 1=mellom, 2=slutt, 3=nåværende/sporing
    public double latitude;
    public double longitude;
    public double altitude;

    public Location(@NonNull double latitude, @NonNull double longitude, @NonNull double altitude, @NonNull long fk_trip, @NonNull int partOfTrip) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.fk_trip = fk_trip;
        this.partOfTrip = partOfTrip;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getFk_trip() {
        return fk_trip;
    }

    public void setFk_trip(long fk_trip) {
        this.fk_trip = fk_trip;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
