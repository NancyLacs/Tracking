package com.example.tracking.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tracking.entities.Location;
import com.example.tracking.entities.Trip;

import java.util.List;

@Dao
public interface TripLocationDAO {
    //New trip
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertTrip(Trip trip);

    @Delete
    void deleteTrip(Trip trip);

    @Update
    void updateTrip(Trip trip);

    @Query("DELETE FROM TRIP WHERE status = 2")
    void deleteOngoingTrips();

    @Query("SELECT * FROM Trip WHERE tripId = :tripId")
    LiveData<Trip> getTripById(long tripId);

    //viser alle ferdige turer (u/ lokasjon)
    @Query("SELECT * FROM Trip WHERE status = 3")
    LiveData<List<Trip>> getAllFinishedTrips();

    //planlagte turer
    @Query("SELECT * FROM Trip WHERE status = 1")
    LiveData<List<Trip>> getPlannedTrips();

    @Query("SELECT * FROM Trip WHERE status = 2")
    LiveData<Trip> getOngoingTrip();

    @Query("SELECT * FROM Trip ORDER BY tripId DESC LIMIT 1")
    LiveData<Trip> getLastCreatedTrip();

    @Query("SELECT * FROM Trip ORDER BY tripId DESC LIMIT 1")
    LiveData<Trip> getNewTrip();

    @Query("SELECT * FROM Trip WHERE tripName = :tripName AND date = :date")
    Trip getNewSpecificTrip(String tripName, String date);

    //oppdaterer varighet
    @Query("UPDATE Trip SET duration = :duration WHERE tripId = :tripId")
    void updateDuration (long tripId, long duration);

    //henter varighet
    @Query("SELECT duration FROM Trip WHERE tripId = :tripId")
    long getDuration (long tripId);

    //oppdaterer lengde
    @Query("UPDATE Trip SET length = :distance WHERE tripId = :tripId")
    void updateLength (long tripId, double distance);

    //legger til lengde
    @Query("UPDATE Trip SET length = :distance + length WHERE tripId = :tripId")
    void addToLength (long tripId, double distance);

    @Query("DELETE FROM Trip WHERE status = 0")
    void deleteTripsToBeRegistered();

    //henter lengde
    @Query("SELECT length FROM Trip WHERE tripId = :tripId")
    int getLength (long tripId);

    //Total lengde
    @Query("SELECT SUM(length) FROM Trip")
    double getTotalLength();

    @Query("SELECT AVG(length) FROM Trip")
    double getAvgLength();

    //Avg toughness
    @Query("SELECT AVG(toughness) FROM Trip")
    double getAvgToughness();

    @Query("SELECT AVG(pace) FROM Trip")
    double getAvgPace();

    //oppdaterer startDato
    @Query("UPDATE Trip SET startTime = :start WHERE tripId = :tripId")
    void updateStartTime (long tripId, String start);

    //henter start
    @Query("SELECT startTime FROM Trip WHERE tripId = :tripId")
    String getStart (long tripId);

    //oppdaterer sluttDato
    @Query("UPDATE Trip SET endTime = :end WHERE tripId = :tripId")
    void updateEndTime (long tripId, String end);

    //henter slutt
    @Query("SELECT endTime FROM Trip WHERE tripId = :tripId")
    String getEnd (long tripId);

    //Oppdaterer trip til ferdig
    @Query("UPDATE Trip SET status = 1 WHERE tripId = :tripId")
    void updateTripToFinished (long tripId);

    //For Location
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertLocation(Location location);

    @Query("DELETE FROM Location WHERE fk_trip = :tripId")
    void deleteLocationsForTrip(long tripId);

    @Query("SELECT * FROM Location WHERE locationId = :locationId")
    Location getLocationById (long locationId);

    //henter alle locations knyttet til en trip
    @Query("SELECT * FROM Location WHERE fk_trip = :tripId ORDER BY partOfTrip ASC")
    LiveData<List<Location>> getLocationsForTrip (long tripId);

    //sletter alle nåværende lokasjoner untatt siste
    @Query("DELETE FROM Location WHERE fk_trip = 2 AND partOfTrip = 5 AND locationId NOT IN (SELECT locationId FROM Location WHERE fk_trip = 2 AND partOfTrip = 5 ORDER BY locationId DESC LIMIT 1)")
    void deleteCurrentLocationsExceptLast();

    @Query("SELECT * FROM Location WHERE fk_trip = 2 AND partOfTrip = 5 ORDER BY locationId")
    LiveData<List<Location>> getCurrentLocations();

    @Query("SELECT * FROM Location WHERE partOfTrip = 0 AND fk_trip = :fk_trip")
    LiveData<Location> getStartLocation(long fk_trip);

    @Query("SELECT * FROM Location WHERE partOfTrip = 3 AND fk_trip = :fk_trip ORDER BY locationId ASC LIMIT 1")
    LiveData<Location> getActualStartLocation(long fk_trip);


}
