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
    private LiveData<Trip> newTrip;
    private LiveData<List<Trip>> plannedTrips;
    private LiveData<List<Trip>> finishedTrips;
    private LiveData<Trip> onGoingTrip;
    private LiveData<Trip> lastCreatedTrip;

    public TripRepository(Application application){
        TripLocationRoomDB db = TripLocationRoomDB.getDatabase(application);
        tripLocationDAO = db.tripLocationDAO();
        newTrip = tripLocationDAO.getNewTrip();
        plannedTrips = tripLocationDAO.getPlannedTrips();
        finishedTrips = tripLocationDAO.getAllFinishedTrips();
        onGoingTrip = tripLocationDAO.getOngoingTrip();
        lastCreatedTrip = tripLocationDAO.getLastCreatedTrip();
    }

    public long insert(Trip trip){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripId = tripLocationDAO.insertTrip(trip);
        });
        return tripId;
    }

    public LiveData<Trip> getOnGoingTrip(){
        return onGoingTrip;
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
        return finishedTrips;
    }

    public LiveData<Trip> getLastCreatedTrip() {
        return lastCreatedTrip;
    }

    public LiveData<Trip> getTripById(long tripId) {
        return tripLocationDAO.getTripById(tripId);
    }

    public LiveData<List<Trip>> getPlannedTrips() {

        return plannedTrips;
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

    public LiveData<Trip> getNewTrip(){
        return newTrip;
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

    public void deleteOngoingTrips(){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.deleteOngoingTrips();
        });
    }

    public LiveData<Location> getStartLocation(long fk_trip){
        return tripLocationDAO.getStartLocation(fk_trip);
    }

}
