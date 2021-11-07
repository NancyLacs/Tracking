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

    @Query("SELECT * FROM Trip WHERE tripId = :tripId")
    LiveData<Trip> getTripById(long tripId);

    //viser alle ferdige turer (u/ lokasjon)
    @Query("SELECT * FROM Trip WHERE status = 1")
    LiveData<List<Trip>> getAllFinishedTrips();

    //planlagte turer
    @Query("SELECT * FROM Trip WHERE status = 0")
    LiveData<List<Trip>> getPlannedTrips();

    //oppdaterer varighet
    @Query("UPDATE Trip SET duration = :duration WHERE tripId = :tripId")
    void updateDuration (long tripId, int duration);

    //henter varighet
    @Query("SELECT duration FROM Trip WHERE tripId = :tripId")
    int getDuration (long tripId);

    //oppdaterer lengde
    @Query("UPDATE Trip SET length = :duration WHERE tripId = :tripId")
    void updateLength (long tripId, double duration);

    //henter lengde
    @Query("SELECT length FROM Trip WHERE tripId = :tripId")
    int getLength (long tripId);

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
    @Query("SELECT * FROM Location WHERE fk_trip = :tripId ORDER BY locationId ASC")
    LiveData<List<Location>> getLocationsForTrip (long tripId);



}
