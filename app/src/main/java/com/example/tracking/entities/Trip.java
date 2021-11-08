package com.example.tracking.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Trip {
    @PrimaryKey(autoGenerate = true)
    public long tripId;
    public String tripName;
    public String date;
    public int status; //0= ny tur 1=under registrering 2=planlagt 3=pågående 4=ferdig
    public double length;
    public int duration;
    public String startTime;
    public String endTime;


    public Trip(@NonNull String tripName, @NonNull String date) {
        this.tripName = tripName;
        this.date = date;
        this.length = 0.0;
        this.duration = 0;
        this.startTime = "";
        this.endTime = "";
        this.status = 0;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
