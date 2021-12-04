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
    public int status; //0= ny tur 1=planlagt 2=pågående 3=ferdig 4=current(henting av nåværende lokasjon)
    public double length;
    public long duration;
    public String startTime;
    public String endTime;
    public double toughness;
    public double pace;
    public double elevation;


    public Trip(@NonNull String tripName, @NonNull String date) {
        this.tripName = tripName;
        this.date = date;
        this.length = 0.0;
        this.duration = 0;
        this.startTime = "";
        this.endTime = "";
        this.status = 0;
        this.toughness = 0.0;
        this.pace = 0.0;
        this.elevation = 0.0;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToughnessInText(){
        String toughnessText = "";
        if(toughness == 0){
            toughnessText = "";
        } else if (toughness < 50){
            toughnessText = "Easiest";
        } else if (toughness < 100){
            toughnessText = "Moderate";
        } else if (toughness < 150){
            toughnessText = "Moderately Strenuous";
        } else{
            toughnessText = "Strenuous";
        }
        return toughnessText;
    }


}
