package com.example.tracking.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TripAndLocations {
    @Embedded
    public Trip trip;
    @Relation(
            parentColumn = "tripId",
            entityColumn = "fk_trip"
    )
    public List<Location> locations;

    public TripAndLocations(Trip trip, List<Location> locations) {
        this.trip = trip;
        this.locations = locations;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
