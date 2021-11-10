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
    private long tripId;


    public TripRepository(Application application){
        TripLocationRoomDB db = TripLocationRoomDB.getDatabase(application);
        tripLocationDAO = db.tripLocationDAO();
    }

    public long insert(Trip trip){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripId = tripLocationDAO.insertTrip(trip);
        });
        return tripId;
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

    public LiveData<List<Trip>> getNewTrip(){
        return tripLocationDAO.getNewTrip();
    }

    public void addToLength(double distance, long tripId){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.addToLength(tripId, distance);
        });
    }

    public void deleteCurrentLocationsExceptLast(){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.deleteCurrentLocationsExceptLast();
        });
    }

    public LiveData<List<Location>> getCurrentLocations (){
        return tripLocationDAO.getCurrentLocations();
    }

    public void deleteTripsToBeRegistered(){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.deleteTripsToBeRegistered();
        });
    }

    public Trip getNewSpecificTrip(String tripName, String date){

        return tripLocationDAO.getNewSpecificTrip(tripName, date);
    }

}
