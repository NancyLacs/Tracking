package com.example.tracking.entities;


import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private int age;
    private double weight;
    private double distanceHiked = 0.0;
    private double averageToughness = 0.0;
    private double totalToughness = 0.0;
    private double averagePace = 0.0;
    private double totalPace = 0.0;


    public Person(String name, int age, double weight) {
        this.name = name;
        this.age = age;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getDistanceHiked() {
        return distanceHiked;
    }

    public void setDistanceHiked(double distanceHiked) {
        this.distanceHiked = distanceHiked;
    }

    public double getAverageToughness() {
        return averageToughness;
    }

    public void setAverageToughness(double averageToughness) {
        this.averageToughness = averageToughness;
    }

    public double getTotalToughness() {
        return totalToughness;
    }

    public void setTotalToughness(double totalToughness) {
        this.totalToughness = totalToughness;
    }

    public double getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(double averagePace) {
        this.averagePace = averagePace;
    }

    public double getTotalPace() {
        return totalPace;
    }

    public void setTotalPace(double totalPace) {
        this.totalPace = totalPace;
    }
}
