package com.example.tracking.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tracking.MainActivity;
import com.example.tracking.R;
import com.example.tracking.entities.Trip;
import com.example.tracking.fragments.MapFragment;
import com.example.tracking.repositories.TripRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class TripLocationService extends Service {

    private static final int LOCATION_NOTIFICATION_ID = 1010;

    private Location locationCheck;
    private long tripStatus;
    private LocationCallback locationCallback;
    private Location previousLocation = null;
    private NotificationManager notificationManager;
    private String channelId;
    private TripRepository tripRepository;
    private FusedLocationProviderClient fusedLocationProviderClient;



    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        Notification notification = createNotification("0.0.0.0");
        startForeground(LOCATION_NOTIFICATION_ID, notification);

        tripRepository = new TripRepository(getApplication());
    }

    private Notification createNotification(String notificationText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("Tracking is running")
                .setOnlyAlertOnce(false)
                .setContentText("Your location: " + notificationText)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setContentIntent(pendingIntent)
                .setTicker("Tracking your location")
                .build();
        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = this.getApplicationContext();
        Log.d("TRIP_SERVICE", "onStartCommand(...)");

        long tripId = intent.getLongExtra("tripId", 0);
        tripStatus = intent.getIntExtra("tripStatus", 0);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()){
                    if(previousLocation == null){
                        previousLocation = location;
                    }

                    double distance = previousLocation.distanceTo(location);

                    previousLocation = location;

                    //Save current location as currentlocation in db
                    tripRepository.insert(new com.example.tracking.entities.Location(location.getLatitude(), location.getLongitude(), location.getAltitude(), 2, 5));

                    //If this trip is ongoing, this trip is tracking
                    if (tripStatus == 3) {
                        tripRepository.insert(new com.example.tracking.entities.Location(location.getLatitude(), location.getLongitude(), location.getAltitude(), tripId, 3));
                        tripRepository.addToLength(distance, tripId);
                    }
                }

                /*Notification notification = createNotification("Lat: " + locationResult.getLastLocation().getLatitude() + " Long: " + locationResult.getLastLocation().getLongitude() + " Alt: " + locationResult.getLastLocation().getAltitude());
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(LOCATION_NOTIFICATION_ID, notification);*/
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //final LocationRequest locationRequest = MapFragment.createLocationRequest();
        //startLocationUpdates(locationRequest);

        return Service.START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.stopLocationUpdates();
        tripRepository.deleteCurrentLocationsExceptLast();
        super.onDestroy();
    }

    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "triplocation_channelid";
        String channelName = "TripLocationService";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates(LocationRequest locationRequest) {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void stopLocationUpdates() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
