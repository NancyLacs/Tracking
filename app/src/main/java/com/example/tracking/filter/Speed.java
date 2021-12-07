package com.example.tracking.filter;

public class Speed {
    private double speedX;
    private double speedZ;
    private double speedY;

    public Speed(double speedX, double speedY, double speedZ){
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedZ() {
        return speedZ;
    }

    public void setSpeedZ(double speedZ) {
        this.speedZ = speedZ;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
}
