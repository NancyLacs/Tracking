package com.example.tracking.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tracking.entities.Location;
import com.example.tracking.entities.Trip;
import com.example.tracking.repositories.TripRepository;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private final TripRepository tripRepository;

    public TripViewModel(@NonNull Application application) {
        super(application);
        this.tripRepository = new TripRepository(application);
    }

    public long insert(Trip trip){
        return tripRepository.insert(trip);
    }

    public void insert(Location location){
        tripRepository.insert(location);
    }

    public void deleteTrip(Trip trip){

        tripRepository.deleteTrip(trip);
    }

    public void updateTrip(Trip trip) {

        tripRepository.updateTrip(trip);
    }

    public LiveData<List<Trip>> getAllFinishedTrips() {
        return tripRepository.getAllFinishedTrips();
    }
    public LiveData<List<Trip>> getPlannedTrips() {

        return tripRepository.getPlannedTrips();
    }

    public LiveData<Trip> getTripById(long tripId) {

        return tripRepository.getTripById(tripId);
    }

    public LiveData<List<Location>> getLocationsForTrip(long tripId){
        return tripRepository.getLocationsForTrip(tripId);
    }
    public void updateTripToFinished(long tripId) {
        tripRepository.updateTripToFinished(tripId);
    }

    public void deleteLocationsForTrip (long tripId){
        tripRepository.deleteLocationsForTrip(tripId);
    }

    public int getDuration(long tripId){

        return tripRepository.getDuration(tripId);
    }

    public void updateDuration (long tripId, int duration){
        tripRepository.updateDuration(tripId, duration);
    }

    public Trip getNewTrip(){
        return tripRepository.getNewTrip();
    }

    public void addToLength(double distance, long tripId){
        tripRepository.addToLength(distance, tripId);
    }

    public LiveData<List<Location>> getCurrentLocations (){
        return tripRepository.getCurrentLocations();
    }

    public void deleteCurrentLocationsExceptLast() {
        tripRepository.deleteCurrentLocationsExceptLast();
    }

    public void deleteTripsToBeRegistered(){
        tripRepository.deleteTripsToBeRegistered();
    }

    public Trip getNewSpecificTrip(String name, String date){
        return tripRepository.getNewSpecificTrip(name, date);
    }
}
