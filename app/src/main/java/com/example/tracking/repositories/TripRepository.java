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
    private long duration;
    private long tripId;
    private LiveData<Trip> newTrip;
    private LiveData<List<Trip>> plannedTrips;
    private LiveData<List<Trip>> finishedTrips;
    private LiveData<Trip> onGoingTrip;
    private LiveData<Trip> lastCreatedTrip;
    private LiveData<Double> totalLength;
    private double avgLength;
    private LiveData<Double> avgToughness;
    private LiveData<Double> avgPace;
    private LiveData<Double> totalCalories;
    private LiveData<Integer> nrOfTrips;
    private LiveData<Integer> totalSteps;

    public TripRepository(Application application){
        TripLocationRoomDB db = TripLocationRoomDB.getDatabase(application);
        tripLocationDAO = db.tripLocationDAO();
        newTrip = tripLocationDAO.getNewTrip();
        plannedTrips = tripLocationDAO.getPlannedTrips();
        finishedTrips = tripLocationDAO.getAllFinishedTrips();
        onGoingTrip = tripLocationDAO.getOngoingTrip();
        lastCreatedTrip = tripLocationDAO.getLastCreatedTrip();
        totalLength = tripLocationDAO.getTotalLength();
        avgToughness = tripLocationDAO.getAvgToughness();
        avgPace = tripLocationDAO.getAvgPace();
        totalCalories = tripLocationDAO.getTotalCalories();
        nrOfTrips = tripLocationDAO.getNrOfTrips();
        totalSteps = tripLocationDAO.getTotalSteps();
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

    public void updateDuration (long tripId, long duration){
        TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            tripLocationDAO.updateDuration(tripId, duration);
        });
    }

    public long getDuration (long tripId){
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

    public LiveData<Location> getActualStartLocation(long fk_trip){
        return tripLocationDAO.getActualStartLocation(fk_trip);
    }

    public LiveData<Double> getTotalLength (){
        /*TripLocationRoomDB.databaseWriteExecutor.execute(()-> {

            totalLength = tripLocationDAO.getTotalLength();

        });*/
        return totalLength;
    }

    public double getAvgLength (){
        TripLocationRoomDB.databaseWriteExecutor.execute(()-> {

            avgLength = tripLocationDAO.getAvgLength();
        });
        return avgLength;
    }

    public LiveData<Double> getAvgToughness (){
        /*TripLocationRoomDB.databaseWriteExecutor.execute(()-> {

            avgToughness = tripLocationDAO.getAvgToughness();
        });*/
        return avgToughness;
    }

    public LiveData<Double> getAvgPace (){
        /*TripLocationRoomDB.databaseWriteExecutor.execute(()-> {

            avgPace = tripLocationDAO.getAvgPace();
        });*/
        return avgPace;
    }

    public LiveData<Double> getTotalCalories(){
        /*TripLocationRoomDB.databaseWriteExecutor.execute(()-> {

            totalCalories = tripLocationDAO.getTotalCalories();
        });*/
        return totalCalories;
    }

    public LiveData<Integer> getNrOfTrips(){
        /*TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            nrOfTrips = tripLocationDAO.getNrOfTrips();
        });*/
        return nrOfTrips;
    }

    public LiveData<Integer> getTotalSteps(){
        /*TripLocationRoomDB.databaseWriteExecutor.execute(()->{
            totalSteps = tripLocationDAO.getTotalSteps();
        });*/
        return totalSteps;
    }
}
