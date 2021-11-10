package com.example.tracking.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.tracking.BuildConfig;
import com.example.tracking.R;
import com.example.tracking.entities.Location;
import com.example.tracking.entities.Trip;
import com.example.tracking.services.TripLocationService;
import com.example.tracking.viewmodel.TripViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MapFragment extends Fragment implements LocationListener {

    private View view;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private static final int CALLBACK_ALL_PERMISSIONS = 1;
    //permissions til lokasjon
    private static String[] requiredLocationPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //Manifest.permission.FOREGROUND_SERVICE
    };

    private TextView tvTripNameMap;

    private MapView map_view;
    // Til kart
    private Marker startMarker;
    private Marker waypointMarker;
    private Marker endMarker;
    private Marker currentPositionMarker; //for person
    private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private Polyline mPolyline;
    private Polyline plannedRoute;
    private int tapOnMapCount = 0;

    private TripViewModel tripViewModel;
    //private Intent service;
    //private Location previousLocation = null;

    //From navigation fragment
    private long tripId;
    private int tripStatus;


    private Trip trip;
    private String tripName;

    private boolean requestingLocationUpdates = false;
    //private FusedLocationProviderClient fusedLocationClient;
    //private LocationCallback locationCallback;

    //Lokalisering
    private LocationManager mLocationManager;
    private boolean autoCentering = true; //slå av og på sentrering
    private boolean tracking = false; //
    private boolean planRoute = false;
    private boolean trackDrawing = false;

    //Kontroller
    private ImageView btAutoCenter, btAdd, btPlay, btStop, btPlanRoute;

    //Dialog for ny tur
    private AlertDialog alertDialog;
    private EditText etTripNameDialog;
    private CalendarView cvTripDate;
    private View dialogView;
    private static final String MY_DATE_FORMAT = "dd.MM.yyyy";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MY_DATE_FORMAT);
    private String selectedDate;
    private Calendar date;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        verifyPermissions();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        this.view = view;
        dialogView = getLayoutInflater().inflate(R.layout.new_trip_dialog, null);
        tripId = MapFragmentArgs.fromBundle(getArguments()).getTripId();
        tripStatus = MapFragmentArgs.fromBundle(getArguments()).getTripStatus();
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        //service = new Intent(requireActivity(), TripLocationService.class);
        tvTripNameMap = view.findViewById(R.id.tvTripNameMap);
        btAutoCenter = view.findViewById(R.id.btCenter);
        btAdd = view.findViewById(R.id.addButton);
        btPlay = view.findViewById(R.id.playButton);
        btStop = view.findViewById(R.id.stopButton);
        btPlanRoute = view.findViewById(R.id.btPlanRoute);
        createDialogForNewTrip();

        //ordinary map, no planned trip, from startFragment
        if (tripId == 0 && tripStatus == 0){
            tracking = false;
            btAdd.setVisibility(View.VISIBLE);
            btPlay.setVisibility(View.VISIBLE);
        }

        // Is trip planned, finished?
        if(tripStatus > 0){
            tripViewModel.getTripById(tripId).observe(getViewLifecycleOwner(), chosenTrip ->{
                this.trip = chosenTrip;
                tripName = this.trip.tripName;
                switch (tripStatus){
                    case 1:
                        tvTripNameMap.setText("Planned trip: " + tripName);
                        btPlay.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        tvTripNameMap.setText("Finished trip:" + tripName);
                        break;
                }
            });
        }

        verifyPermissions();

        btPlanRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planRoute = true;
                tvTripNameMap.setText("Locate start point for " + trip.tripName);
                btPlanRoute.setVisibility(View.GONE);
            }
        });


        btAutoCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCentering();
            }
        });

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(planRoute){
                    Toast.makeText(requireContext(), "End of planning, start of tracking", Toast.LENGTH_SHORT).show();
                    planRoute = false;
                }
                if(trip == null || (trip != null && trip.status == 0)){
                    tracking = true;
                } else if (trip!=null && trip.status == 1){
                    trip.status = 2;
                    tripViewModel.updateTrip(trip);
                    tracking = true;
                }
                else {
                    tracking = false;
                }
                btPlay.setVisibility(View.GONE);
                btStop.setVisibility(View.VISIBLE);
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tracking = false;
                Toast.makeText(requireContext(), "Tracking is ended.", Toast.LENGTH_SHORT).show();
                if (trip == null){
                    btPlay.setVisibility(View.VISIBLE);
                    btStop.setVisibility(View.GONE);
                }
            }
        });



        /*btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat formatter= new SimpleDateFormat("dd.MM.yyyy");
                Random random = new Random();
                int randomInt = random.nextInt(100);
                Date date = new Date(System.currentTimeMillis());
                String now = formatter.format(date);
                String name = "NEW-" + randomInt;
                long inserted = tripViewModel.insert(new Trip(name, now));
                Toast.makeText(requireContext(), "Inserted: "+ inserted, Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(v);
                MapFragmentDirections.ActionMapFragmentToNewTripFragment actionMapFragmentToNewTripFragment =
                        MapFragmentDirections.actionMapFragmentToNewTripFragment();
                actionMapFragmentToNewTripFragment.setNewTripName(name);
                actionMapFragmentToNewTripFragment.setNewDate(now);
                navController.navigate(actionMapFragmentToNewTripFragment);
            }
        });*/
        //updateCurrentLocation();


        /*locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
                    if (currentPositionMarker != null) {
                        currentPositionMarker.setPosition(geoPoint);
                        map_view.getOverlays().add(currentPositionMarker);
                    }
                    if (autoCentering){
                        map_view.getController().setCenter(geoPoint);
                    }

                }
            }
        };*/
    }


    private void createDialogForNewTrip(){
        alertDialog = new AlertDialog.Builder(requireContext()).create();
        alertDialog.setTitle("New Trip");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Fill in the name and date of trip");

        etTripNameDialog = dialogView.findViewById(R.id.etTripNameDialog);
        cvTripDate = dialogView.findViewById(R.id.cvTripDateDialog);
        cvTripDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "." + (month+1) + "." + year;
                try{
                    date = Calendar.getInstance();
                    date.setTime(simpleDateFormat.parse(selectedDate));
                    Toast.makeText(requireContext(), "OK Calendar", Toast.LENGTH_SHORT).show();
                }catch (ParseException e){
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "ParseException: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tripName = etTripNameDialog.getText().toString();
                if(!tripName.equals("") && date != null){
                    String dateString = new SimpleDateFormat(MY_DATE_FORMAT).format(date.getTime());
                    Trip newTrip = new Trip(tripName, dateString);
                    tripId = tripViewModel.insert(newTrip);
                    tvTripNameMap.setText(newTrip.tripName);
                    btAdd.setVisibility(View.GONE);
                    btPlanRoute.setVisibility(View.VISIBLE);
                    trip = tripViewModel.getNewTrip();
                    //btPlay.setVisibility(View.GONE);
                } else if(!tripName.equals("") && date == null){
                    Toast.makeText(requireContext(), "You must choose a date.", Toast.LENGTH_SHORT).show();
                } else if(tripName.equals("") && date != null){
                    Toast.makeText(requireContext(), "You must provide a trip name.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "You must fill in the necessary information.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(requireContext(), "SAVE new trip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runTracking(GeoPoint gp){
        mPolyline.addPoint(gp);
        map_view.invalidate();
    }

    private void initMap(View view) {
        Log.d("INITMAP", "initmap");
        //final Context ctx = requireActivity().getApplicationContext();
        //Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map_view = view.findViewById(R.id.map_view);
        startMarker = new Marker(map_view);//for start marker
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setTitle("Startpunkt");
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_start_sign, null));


        waypointMarker = new Marker(map_view);
        waypointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        waypointMarker.setTitle("Mellompunkt");
        waypointMarker.setIcon(getResources().getDrawable(R.drawable.ic_waypoint, null));

        endMarker = new Marker(map_view);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        endMarker.setTitle("Sluttpunkt");
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_flag, null));

        currentPositionMarker = new Marker(map_view);
        currentPositionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        currentPositionMarker.setTitle("Min posisjon");
        currentPositionMarker.setIcon(getResources().getDrawable(R.drawable.ic_my_location, null));

        this.mPolyline = new Polyline(map_view);

        final Paint paintInside = new Paint();
        paintInside.setStrokeWidth(7);
        paintInside.setStyle(Paint.Style.FILL);
        paintInside.setColor(Color.RED);
        paintInside.setStrokeCap(Paint.Cap.ROUND);
        paintInside.setAntiAlias(true);
        mPolyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintInside));
        map_view.getOverlays().add(mPolyline);

        //map_view.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map_view.setTileSource(TileSourceFactory.MAPNIK);
        map_view.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map_view.setMultiTouchControls(true);
        map_view.getController().setZoom(14.0);


        // Compass overlay;
        this.mCompassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), map_view);
        this.mCompassOverlay.enableCompass();
        map_view.getOverlays().add(this.mCompassOverlay);

        // Multi touch:
        mRotationGestureOverlay = new RotationGestureOverlay(getContext(), map_view);
        mRotationGestureOverlay.setEnabled(true);
        map_view.setMultiTouchControls(true);
        map_view.getOverlays().add(this.mRotationGestureOverlay);

        // Zoom-knapper;
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(map_view);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map_view.getOverlays().add(this.mScaleBarOverlay);
        //map_view.setVerticalMapRepetitionEnabled(false);

        plannedRoute = new Polyline(map_view);
        final Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        plannedRoute.getOutlinePaintLists().add(new MonochromaticPaintList(paint));
        map_view.getOverlays().add(plannedRoute);

        /*if (tripId > 0) { //når man skal gå inn på kart tripId
            chooseActionByStatus();
        }*/
        //initLocationUpdates();
        //requireContext().startForegroundService(service);

        addSingleTapOnPlanning();
    }


    private void addSingleTapOnPlanning() {
        final MapEventsReceiver planMapReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                if (planRoute){
                    if (tapOnMapCount == 0 && tripStatus == 0 && tripId == 0) {
                        startMarker.setPosition(geoPoint);
                        map_view.getOverlays().add(startMarker);
                        Location startLocation = new Location(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude(), trip.tripId, 0);
                        tripViewModel.insert(startLocation);
                        tvTripNameMap.setText("LOCATE THE ENDPOINT for " + trip.tripName);
                        tapOnMapCount++;
                    } else if (tapOnMapCount == 1 && tripStatus == 0 && tripId == 0) {
                        endMarker.setPosition(geoPoint);
                        map_view.getOverlays().add(endMarker);
                        Location endLocation = new Location(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude(), trip.tripId, 2);
                        tripViewModel.insert(endLocation);
                        tvTripNameMap.setText("Locate waypoints or start tracking." + trip.tripName);
                        tapOnMapCount++;
                        trip.status = 1;
                        tripViewModel.updateTrip(trip);
                        btPlay.setVisibility(View.VISIBLE);
                    } else { //mellompunkter
                        Marker waypointsMarker = new Marker(map_view);
                        waypointsMarker.setPosition(geoPoint);
                        waypointsMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        map_view.getOverlays().add(waypointsMarker);
                        waypointsMarker.setIcon(getResources().getDrawable(R.drawable.ic_waypoint, null));
                        waypointsMarker.setTitle("Waypoint");
                        Location wayPoint = new Location(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude(), trip.tripId, 2);
                        tripViewModel.insert(wayPoint);
                    }
                }

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map_view.getOverlays().add(new MapEventsOverlay(planMapReceiver));
    }



    private void toggleCentering(){
        if(autoCentering){
            autoCentering = false;
            Toast.makeText(requireContext(), "Autocentering is switched of.", Toast.LENGTH_SHORT).show();
        } else {
            autoCentering = true;
            Toast.makeText(requireContext(), "Autocentering is switched on.", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        Boolean networkStateAccess = result.getOrDefault(Manifest.permission.ACCESS_NETWORK_STATE, false);
                        Boolean wifiAccess = result.getOrDefault(Manifest.permission.ACCESS_WIFI_STATE, false);
                        Boolean writeExternal = result.getOrDefault(Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
                        Boolean readExternal = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            requestingLocationUpdates = true;
                            //initLocationUpdates();
                            //requireContext().startForegroundService(service);
                            requestLocationUpdates();
                            initMap(view);
                            //requireContext().startForegroundService(service);

                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );

    private boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void verifyPermissions() {
        // Kontrollerer om vi har tilgang til eksternt område:
        if (!hasPermissions(requiredLocationPermissions)) {
            //requestPermissions(requiredLocationPermissions, CALLBACK_ALL_PERMISSIONS);
            locationPermissionRequest.launch(requiredLocationPermissions);
        } else {
            //requireContext().startForegroundService(service);
            requestingLocationUpdates = true;
            //initLocationUpdates();
            requestLocationUpdates();
            initMap(view);

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("CURRENT", "onPause");
        mLocationManager.removeUpdates(this);
        tripViewModel.deleteTripsToBeRegistered();
        tripViewModel.deleteCurrentLocationsExceptLast();
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        if(requestingLocationUpdates){
            GeoPoint gp = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
            if (currentPositionMarker != null) {
                currentPositionMarker.setPosition(gp);
                currentPositionMarker.setTitle(location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude());
                map_view.getOverlays().add(currentPositionMarker);
            }
            if (tracking){
                runTracking(gp);
                if(trip!=null && trip.status == 2){
                    tripViewModel.insert(new Location(location.getLatitude(), location.getLongitude(), location.getAltitude(), tripId, 3));
                }
            }
            if (autoCentering){
                map_view.getController().setCenter(gp);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<android.location.Location> locations) {
        Log.d("CURRENT", locations.size() + "");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("CURRENT", "providerEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("CURRENT", "providerDisabled: " + provider);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }


    /*private void updateCurrentLocation(){
        Log.d("UPDATECURRENT", "Locationupdate");
        tripViewModel.getCurrentLocations().observe(getViewLifecycleOwner(), currentLocations -> {
            for (Location location : currentLocations){
                if (previousLocation == null) {
                    previousLocation = location;
                }
                previousLocation = location;
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
                if (currentPositionMarker != null) {
                    currentPositionMarker.setPosition(geoPoint);
                    map_view.getOverlays().add(currentPositionMarker);
                }
                if (autoCentering){
                    map_view.getController().setCenter(geoPoint);
                }
            }
        });
    }*/


 /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Kalles når bruker har akseptert og gitt tillatelse til bruk av posisjon:
            case REQUEST_CHECK_SETTINGS:
                Toast.makeText(requireActivity(), "INITLOCATIONS", Toast.LENGTH_SHORT).show();
                initLocationUpdates();
                return;
        }
    }*/
    /*private void initLocationUpdates() {
        Log.d("CURRENT", "initLOcationUpdates ");
        final LocationRequest locationRequest = this.createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // NB! Sjekker om kravene satt i locationRequest kan oppfylles:
        SettingsClient client = LocationServices.getSettingsClient(requireContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<LocationSettingsResponse>() {

            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Alle lokasjopnsinnstillinger er OK, klienten kan nå initiere lokasjonsforespørsler her:
                Log.d("CURRENT", "initLOcationUpdates2 ");
                //startLocationUpdates(locationRequest);
            }
        });
        task.addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Lokasjopnsinnstillinger er IKKE OK, men det kan fikses ved å vise brukeren en dialog!!
                    try {
                        Log.d("CURRENT", "initLOcationUpdatesFailure try ");
                        // Viser dialogen ved å kalle startResolutionForResult() OG SJEKKE resultatet i onActivityResult()
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.d("CURRENT", "initLOcationUpdates Failure catch");
                    }
                }
            }
        });
        requireContext().startForegroundService(service);

    }


    //kalles av TripLocationService
    public static LocationRequest createLocationRequest() {

        LocationRequest locationRequest = LocationRequest.create();
        // Hvor ofte ønskes lokasjonsoppdateringer (her: hvert 10.sekund)
        locationRequest.setInterval(5 * 1000);
        // Her settes intervallet for hvor raskt appen kan håndtere oppdateringer.
        locationRequest.setFastestInterval(3 * 1000);
        // Ulike verderi; Her: høyest mulig nøyaktighet som også normalt betyr bruk av GPS.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("CURRENT", "createLocationRequest");
        return locationRequest;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("CURRENT", "serviceRunning");
                return true;
            }
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            verifyPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(LocationRequest locationRequest) {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }*/
}