package com.example.tracking.filter;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KalmanFilter {

    //Kalman gains
    //position kalman gain
    private double errorOfEstimatePosition; // state estimate error
    private double errorOfMeasurementPosition; //measurement error
    private double kalmanGainPosition;

    //acceleration kalman gain
    private double errorOfEstimateAcceleration;
    private double errorOfMeasurementAcceleration;
    private double kalmanGainAcceleration;

    //speed kalman gain
    private double errorOfEstimateSpeed;
    private double errorOfMeasurementSpeed;
    private double kalmanGainSpeed;

    //for location update and measured location
    private Location currentEstimatedLocation;
    private Location previousEstimatedLocation;
    private ArrayList<Location> measuredLocation = new ArrayList<Location>(); //based on GPS measurement

    //for acceleration update and measured acceleration from sensor
    private AccelerationSensor currentEstimatedAcceleration;
    private AccelerationSensor previousEstimatedAcceleration;
    private ArrayList<AccelerationSensor> measuredAcceleration;

    //for speed update and measured speed from sensor
    private Speed currentEstimatedSpeed;
    private Speed previousEstimatedSpeed;
    private ArrayList<Speed> measuredSpeed;

    //for delta time in calculating speed and predicted location
    private Date previousTime;
    private Date currentTime;
    private double deltaTime; //in seconds



    public KalmanFilter (double errorOfEstimate, double errorOfMeasurement, Location initialPosition){
        this.errorOfEstimatePosition = errorOfEstimate;
        this.errorOfMeasurementPosition = errorOfMeasurement;
        //this.kalmanGainPosition = errorOfEstimate/(errorOfEstimate+errorOfMeasurement);
        this.previousEstimatedLocation = initialPosition;
        this.currentEstimatedLocation = initialPosition;
        measuredLocation.add(initialPosition);
        this.previousEstimatedAcceleration = this.currentEstimatedAcceleration = new AccelerationSensor(0.0, 0.0, 0.0);
        this.currentEstimatedSpeed = this.previousEstimatedSpeed = new Speed(0.0, 0.0, 0.0);
    }

    public Location updatePrediction(Location measuredLocation, AccelerationSensor measuredAcceleration){
        if (previousTime == null && currentTime == null){
            previousTime = currentTime = new Date();
        } else{
            currentTime = new Date();
        }
        deltaTime = ((currentTime.getTime() - previousTime.getTime())/1000)%60;
        this.measuredLocation.add(measuredLocation);
        this.measuredAcceleration.add(measuredAcceleration);
        updateKalmanGain();
        updatePosition(); //for location prediction
        updateAcceleration(); //for location prediction
        updateSpeed(); //for location prediction
        updateNewStateEstimateError(); //for next round
        previousTime = currentTime; //for next round
        updatePreviousEstimatesToCurrent(); //for next round
        return getPredictedLocation();
    }

    public Location getPredictedLocation(){
        //fra likningen: x = x0 + v0t + (at^2)/2
        double latitude = currentEstimatedLocation.getLongitude() + (currentEstimatedSpeed.getSpeedX() * deltaTime) +
                ((currentEstimatedAcceleration.getAccelerationX()*(deltaTime*deltaTime))/2);
        double longitude = currentEstimatedLocation.getLatitude() + (currentEstimatedSpeed.getSpeedY() * deltaTime) +
                ((currentEstimatedAcceleration.getAccelerationY()*(deltaTime*deltaTime))/2);
        double altitude = currentEstimatedLocation.getAltitude() + (currentEstimatedSpeed.getSpeedZ() * deltaTime) +
                ((currentEstimatedAcceleration.getAccelerationZ()*(deltaTime*deltaTime))/2);
        Location newLocation = new Location("newLocation");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        newLocation.setAltitude(altitude);
        return newLocation;
    }

    public void updateNewStateEstimateError(){
        errorOfEstimatePosition = (1-kalmanGainPosition) * errorOfEstimatePosition;
        errorOfEstimateAcceleration = (1-kalmanGainAcceleration) * errorOfEstimateAcceleration;
        errorOfEstimateSpeed = (1-kalmanGainSpeed) * errorOfEstimateSpeed;
    }

    public void updateKalmanGain(){
        kalmanGainPosition = errorOfEstimatePosition/(errorOfEstimatePosition+errorOfMeasurementPosition);
        kalmanGainAcceleration = errorOfEstimateAcceleration/(errorOfEstimateAcceleration+errorOfMeasurementAcceleration);
        kalmanGainSpeed = errorOfEstimateSpeed/(errorOfEstimateSpeed+errorOfMeasurementSpeed);
    }

    public void updatePosition(){
        //Update latitude
        currentEstimatedLocation.setLatitude(previousEstimatedLocation.getLatitude() +
                kalmanGainPosition * (measuredLocation.get(measuredLocation.size()-1).getLatitude() - previousEstimatedLocation.getLatitude()));
        //Update longitude
        currentEstimatedLocation.setLongitude(previousEstimatedLocation.getLongitude() +
                kalmanGainPosition * (measuredLocation.get(measuredLocation.size()-1).getLongitude() - previousEstimatedLocation.getLongitude()));
        //Update altitude
        currentEstimatedLocation.setAltitude(previousEstimatedLocation.getAltitude() +
                kalmanGainPosition * (measuredLocation.get(measuredLocation.size()-1).getAltitude() - previousEstimatedLocation.getAltitude()));
    }

    public void updateAcceleration(){
        //update x
        currentEstimatedAcceleration.setAccelerationX(previousEstimatedAcceleration.getAccelerationX() +
                kalmanGainAcceleration * (measuredAcceleration.get(measuredAcceleration.size()-1).getAccelerationX()
                        - previousEstimatedAcceleration.getAccelerationX()));
        //update y
        currentEstimatedAcceleration.setAccelerationY(previousEstimatedAcceleration.getAccelerationY() +
                kalmanGainAcceleration * (measuredAcceleration.get(measuredAcceleration.size()-1).getAccelerationY()
                        - previousEstimatedAcceleration.getAccelerationY()));
        //update z
        currentEstimatedAcceleration.setAccelerationZ(previousEstimatedAcceleration.getAccelerationZ() +
                kalmanGainAcceleration * (measuredAcceleration.get(measuredAcceleration.size()-1).getAccelerationZ()
                        - previousEstimatedAcceleration.getAccelerationZ()));
    }


    public void updateSpeed(){
        if(measuredSpeed.size() == 0){
            measuredSpeed.add(new Speed(0.0, 0.0, 0.0));
        }
        //Calculate measured speed based on measured acceleration and deltaTime
        double speedX = measuredSpeed.get(measuredSpeed.size()-1).getSpeedX() +
                (measuredAcceleration.get(measuredAcceleration.size()-1).getAccelerationX()*deltaTime);
        double speedY = measuredSpeed.get(measuredSpeed.size()-1).getSpeedY() +
                (measuredAcceleration.get(measuredAcceleration.size()-1).getAccelerationY()*deltaTime);
        double speedZ = measuredSpeed.get(measuredSpeed.size()-1).getSpeedZ() +
                (measuredAcceleration.get(measuredAcceleration.size()-1).getAccelerationZ()*deltaTime);
        measuredSpeed.add(new Speed (speedX, speedY, speedZ));
        //Get estimated current estimated speed
        currentEstimatedSpeed.setSpeedX(previousEstimatedSpeed.getSpeedX() +
                kalmanGainSpeed * (measuredSpeed.get(measuredSpeed.size()-1).getSpeedX()
                - previousEstimatedSpeed.getSpeedX()));
        currentEstimatedSpeed.setSpeedY(previousEstimatedSpeed.getSpeedY() +
                kalmanGainSpeed * (measuredSpeed.get(measuredSpeed.size()-1).getSpeedY()
                        - previousEstimatedSpeed.getSpeedY()));
        currentEstimatedSpeed.setSpeedZ(previousEstimatedSpeed.getSpeedZ() +
                kalmanGainSpeed * (measuredSpeed.get(measuredSpeed.size()-1).getSpeedZ()
                        - previousEstimatedSpeed.getSpeedZ()));
    }

    public void updatePreviousEstimatesToCurrent(){
        previousEstimatedLocation = currentEstimatedLocation;
        previousEstimatedAcceleration = currentEstimatedAcceleration;
        previousEstimatedSpeed = currentEstimatedSpeed;
    }


}
