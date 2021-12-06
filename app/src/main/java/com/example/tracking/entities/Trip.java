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
    public double length; // meter
    public long duration; //sekunder
    public String startTime;
    public String endTime;
    public double toughness; //
    public double pace; //m/s
    public double elevation;
    public double met;// metabolic equivalent task (MET) for å regne ut kalorie
    public double extraLoad;
    public double calories;


    public Trip(@NonNull String tripName, @NonNull String date, double extraLoad) {
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
        this.met = 2.0; //vanlig gåing, endres etter pace
        this.extraLoad = extraLoad; // ekstra vekt på tur
        this.calories = 0.0;
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

    public void setMETBasedOnPace(){ // met chart: https://hikingandfishing.com/hiking-calories-burned-calculator/
        double paceInKmH = pace * (3600/1000);
        if (pace == 0.0){ //https://www.topendsports.com/weight-loss/energy-met.htm
            met = 1.3;
        } else if(elevation < 0.0){ //https://www.topendsports.com/weight-loss/energy-met.htm
            met = 2.5;
        } else if(paceInKmH<=2.7 && elevation == 0.0){
            met = 2.3;
        } else if(paceInKmH <= 4){
            met = 2.9;
        } else if (paceInKmH <= 4.8){
            met = 3.3;
        } else if (paceInKmH <= 5.5){
            met = 3.6;
       //https://www.healthline.com/health/average-jogging-speed
        } else if (paceInKmH < 9.6){
            met = 7;
        } else {
            met = 8;
        }
    }

    public void setCaloriesBasedOnTrip(double weight){ //Kalorier brent på tur https://hikingandfishing.com/hiking-calories-burned-calculator/, https://greatist.com/fitness/hiking-calories-burned
        calories = met * (weight + extraLoad) * (duration/3600.00);
    }


}
