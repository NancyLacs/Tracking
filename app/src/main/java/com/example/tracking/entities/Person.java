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


}
