package com.example.tracking.filter;

public class AccelerationSensor {
    private double accelerationX;
    private double accelerationY;
    private double accelerationZ;

    public AccelerationSensor(double accelerationX, double accelerationY, double accelerationZ){
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(double accelerationX) {
        this.accelerationX = accelerationX;
    }

    public double getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(double accelerationY) {
        this.accelerationY = accelerationY;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(double accelerationZ) {
        this.accelerationZ = accelerationZ;
    }
}
