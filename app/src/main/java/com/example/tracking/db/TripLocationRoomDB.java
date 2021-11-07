package com.example.tracking.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.tracking.dao.TripLocationDAO;
import com.example.tracking.entities.Location;
import com.example.tracking.entities.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Trip.class, Location.class}, version = 1)
public abstract class TripLocationRoomDB extends RoomDatabase {
    public abstract TripLocationDAO tripLocationDAO();
    private static volatile TripLocationRoomDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static final String MY_DATE_FORMAT = "dd.MM.yyyy HH:mm";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MY_DATE_FORMAT);

    public static TripLocationRoomDB getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (TripLocationRoomDB.class){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TripLocationRoomDB.class, "trip_location_db")
                        .addCallback(sRoomDatabaseCallback)
                        .build();
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(()->{
                TripLocationDAO dao = INSTANCE.tripLocationDAO();

                //Legg til en trip som kan brukes som test (eller lignende) senere
                Date now = new Date();
                Trip trip1 = new Trip("Testing", simpleDateFormat.format(now));
                long tripId1 = dao.insertTrip(trip1);

            });
        }
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
        };


}
