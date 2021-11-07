package com.example.tracking.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tracking.dao.TripLocationDAO;
import com.example.tracking.db.TripLocationRoomDB;
import com.example.tracking.entities.Location;
import com.example.tracking.entities.Trip;

import java.util.List;

public class TripRepository {

    private TripLocationDAO tripLocationDAO;
    private int duration;


    public TripRepository(Application application){
        TripLocationRoomDB db = TripLocationRoomDB.getDatabase(application);
        tripLocationDAO = db.tripLocationDAO();
    }

    public void insert(Trip trip){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.insertTrip(trip);
        });
    }

    public void insert(Location location){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.insertLocation(location);
        });
    }

    public void deleteTrip(Trip trip) {
        TripLocationRoomDB.databaseWriteExecutor.execute(() -> {
            tripLocationDAO.deleteTrip(trip);
        });
    }

    public void updateTrip(Trip trip) {
        TripLocationRoomDB.databaseWriteExecutor.execute(() -> {
            tripLocationDAO.updateTrip(trip);
        });
    }

    public LiveData<List<Trip>> getAllFinishedTrips() {
        return tripLocationDAO.getAllFinishedTrips();
    }

    public LiveData<Trip> getTripById(long tripId) {
        return tripLocationDAO.getTripById(tripId);
    }

    public LiveData<List<Trip>> getPlannedTrips() {

        return tripLocationDAO.getPlannedTrips();
    }

    public LiveData<List<Location>> getLocationsForTrip(long tripId){
        return tripLocationDAO.getLocationsForTrip(tripId);
    }

    public void updateTripToFinished(long tripId){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.updateTripToFinished(tripId);
        });
    }

    public void deleteLocationsForTrip(long tripId){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.deleteLocationsForTrip(tripId);
        });
    }

    public void updateDuration (long tripId, int duration){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.updateDuration(tripId, duration);
        });
    }

    public int getDuration (long tripId){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            duration = tripLocationDAO.getDuration(tripId);
        });
        return duration;
    }



}
