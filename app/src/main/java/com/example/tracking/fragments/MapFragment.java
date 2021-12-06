package com.example.tracking.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.tracking.BuildConfig;
import com.example.tracking.R;
import com.example.tracking.entities.Location;
import com.example.tracking.entities.Person;
import com.example.tracking.entities.Trip;

import com.example.tracking.viewmodel.TripViewModel;


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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MapFragment extends Fragment implements LocationListener {

    private View view;

    //Person
    private Person user;
    private File file;
    private final String PERSON_FILE = "user.txt";

    //permissions til lokasjon
    private static String[] requiredLocationPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
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
    boolean chosenStart = false; //for tapsingle....
    boolean chosenEnd = false;

    private TripViewModel tripViewModel;

    //From navigation fragment
    private long tripIdFromNavigation;
    private int tripStatusFromNavigation;

    private Trip trip;
    private String tripName;

    //Trip info dialog
    private AlertDialog tripDialog;
    private View tripDialogView;
    private TextView tvTripDialogName, tvTripDialogStatus, tvTripDialogStart, tvTripDialogFinish, tvTripDialogDistance,
            tvTripDialogDuration, tvTripDialogToughness, tvTripDialogPace, tvTripDialogPlanned,
            tvTripDialogBackpack, tvTripDialogCalories, tvTripDialogElevation;
    private boolean requestingLocationUpdates = false;

    //Lokalisering
    private LocationManager mLocationManager;
    private boolean autoCentering = true; //slå av og på sentrering
    private boolean tracking = false; //
    private boolean planRoute = false;
    private boolean registerMode = false;
    private android.location.Location previousLocation;
    private android.location.Location currentLocation;
    private AlertDialog permissionDialog;
    private boolean showMapOnly = false;

    //For planlagte turer
    private Location startLocation;
    private float distanceToStartLoc;

    //Aktuell tur
    private Location actualStartLocation;

    //Kontroller
    private ImageView btAutoCenter, btAdd, btPlay, btStop, btPlanRoute, btSave, btInfo;

    //Dialog for ny tur
    private AlertDialog alertDialog;
    private EditText etTripNameDialog;
    private EditText etTripBackPack;
    private CalendarView cvTripDate;
    private View dialogView;
    private static final String MY_DATE_FORMAT = "dd.MM.yyyy";
    private static final String START_END_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MY_DATE_FORMAT);
    private String selectedDate;
    private Calendar date;

    //Sensor


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
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        dialogView = getLayoutInflater().inflate(R.layout.new_trip_dialog, null);
        tripDialogView = getLayoutInflater().inflate(R.layout.trip_detail_layout, null);
        tripIdFromNavigation = MapFragmentArgs.fromBundle(getArguments()).getTripId();
        tripStatusFromNavigation = MapFragmentArgs.fromBundle(getArguments()).getTripStatus();
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tvTripNameMap = view.findViewById(R.id.tvTripNameMap);
        btAutoCenter = view.findViewById(R.id.btCenter);
        btAdd = view.findViewById(R.id.addButton);
        btPlay = view.findViewById(R.id.playButton);
        btStop = view.findViewById(R.id.stopButton);
        btPlanRoute = view.findViewById(R.id.btPlanRoute);
        btSave = view.findViewById(R.id.btSaveToPlanned);
        btInfo = view.findViewById(R.id.infoButton);
        createDialogForLocationRequirement();
        createDialogForTripDetails();
        initMap(view);
        verifyPermissions();
        createDialogForNewTrip();
        String path = requireContext().getFilesDir().getAbsolutePath();
        file = new File(path, PERSON_FILE);
        readPersonData();
        Toast.makeText(requireContext(), user.getName(),Toast.LENGTH_SHORT).show();

        //ordinary map, no planned trip, from startFragment
        if (tripIdFromNavigation == 0 && tripStatusFromNavigation == 0){
            /*tripViewModel.getLastCreatedTrip().observe(getViewLifecycleOwner(), new Observer<Trip>() {
                @Override
                public void onChanged(Trip t) {
                    trip = t;
                }
            });*/
            tracking = false;
            btAdd.setVisibility(View.VISIBLE);
            btPlay.setVisibility(View.VISIBLE);
        }

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                MapFragmentDirections.ActionMapFragmentToPlannedTripsFragment action = MapFragmentDirections.actionMapFragmentToPlannedTripsFragment();
                action.setTripStatus(1);
                navController.navigate(action);
            }
        });

        btPlanRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planRoute = true;
                btPlanRoute.setVisibility(View.GONE);
                btPlay.setVisibility(View.GONE);
                tvTripNameMap.setText("Locate start point for " + trip.tripName);
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
                btPlanRoute.setVisibility(View.GONE);
                getActualStartLocation();
                if(tripStatusFromNavigation == 0 && !registerMode){
                    tracking = true;
                    Toast.makeText(requireContext(), "Your tracks are drawn but are not saved.", Toast.LENGTH_SHORT).show();
                    btStop.setVisibility(View.VISIBLE);
                    btPlay.setVisibility(View.GONE);

                } else if ((tripStatusFromNavigation == 0 && registerMode && trip.status == 1) ||
                        (tripStatusFromNavigation == 0 && registerMode && trip.status == 0 )|| tripStatusFromNavigation == 1){
                    if(startLocation != null){
                        android.location.Location startLoc = new android.location.Location("startLoc");
                        startLoc.setLatitude(startLocation.getLatitude());
                        startLoc.setLongitude(startLocation.getLongitude());
                        startLoc.setAltitude(startLocation.getAltitude());
                        distanceToStartLoc = currentLocation.distanceTo(startLoc);
                    }

                    if(distanceToStartLoc > 10000 ){
                        Toast.makeText(requireContext(), "You are far from the start point.", Toast.LENGTH_SHORT).show();
                        tracking = false;
                    } else{
                        Date start = new Date();
                        String dateString = new SimpleDateFormat(START_END_DATE_FORMAT).format(start);
                        trip.status = 2;
                        trip.startTime = dateString;
                        tvTripNameMap.setText("Tracking: " + trip.tripName);
                        tripViewModel.updateTrip(trip);
                        tracking = true;
                        chosenStart = false;
                        chosenEnd = false;
                        btStop.setVisibility(View.VISIBLE);
                        btPlay.setVisibility(View.GONE);
                    }
                }
                else {
                    tracking = false;
                    btStop.setVisibility(View.VISIBLE);
                    btPlay.setVisibility(View.GONE);
                }
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tracking = false;
                Toast.makeText(requireContext(), "Tracking is stopped.", Toast.LENGTH_SHORT).show();
                btStop.setVisibility(View.GONE);
                if (tripStatusFromNavigation==0 && !registerMode){
                    btPlay.setVisibility(View.VISIBLE);
                }
                else if (trip.status == 2 || tripStatusFromNavigation==1){
                    tvTripNameMap.setText("Finished trip: " + tripName);
                    try{
                        Date start = convertStringToDate(trip.startTime);
                        Date end = new Date();
                        Toast.makeText(requireContext(), end.getTime() + ", " + start.getTime(), Toast.LENGTH_SHORT).show();
                        long msDifference = end.getTime()-start.getTime();
                        long secDiff = (msDifference/1000)%60;
                        String dateString = new SimpleDateFormat(START_END_DATE_FORMAT).format(end);
                        double startAltitude = actualStartLocation.altitude;
                        double currentAltitude = currentLocation.getAltitude();
                        double altitudeDiff = currentAltitude - startAltitude;
                        double distance = mPolyline.getDistance();
                        double toughness = Math.sqrt((altitudeDiff/3.281) * 2 * ((distance/1000)/1.609));
                        trip.status = 3;
                        trip.endTime = dateString;
                        trip.length =  distance;//meter
                        trip.duration = secDiff;
                        trip.pace = trip.length/(double)secDiff; //m/s
                        trip.elevation = altitudeDiff;
                        trip.toughness = toughness;
                        trip.setMETBasedOnPace();
                        trip.setCaloriesBasedOnTrip(user.getWeight());
                        tripViewModel.updateTrip(trip);
                        NavController navController = Navigation.findNavController(view);
                        MapFragmentDirections.ActionMapFragmentToPlannedTripsFragment action = MapFragmentDirections.actionMapFragmentToPlannedTripsFragment();
                        action.setTripStatus(3);
                        navController.navigate(action);
                    } catch(ParseException e){
                        Toast.makeText(requireContext(), "ParseException: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trip!=null){
                    tripDialog.show();
                } else{
                    Toast.makeText(requireContext(),"The trip detail is not yet ready.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Date convertStringToDate(String dateString) throws ParseException {
        Date date = new SimpleDateFormat(START_END_DATE_FORMAT).parse(dateString);
        return date;
    }

    private void getActualStartLocation(){
        if(trip!=null){
            tripViewModel.getActualStartLocation(trip.tripId).observe(getViewLifecycleOwner(), actualStartLocation ->{
                this.actualStartLocation = actualStartLocation;
            });
        }
    }

    private void initTripObserver(){
        // Is trip planned, finished?
        if(tripStatusFromNavigation > 0){
            tripViewModel.getTripById(tripIdFromNavigation).observe(getViewLifecycleOwner(), chosenTrip ->{
                this.trip = chosenTrip;
                tripName = chosenTrip.tripName;
                setTripDetails();
                //btInfo.setVisibility(View.VISIBLE);
                if(tripStatusFromNavigation == 1){
                    tvTripNameMap.setText("Planned trip: " + tripName);
                    btPlay.setVisibility(View.VISIBLE);
                }
                if(tripStatusFromNavigation == 3){
                    tvTripNameMap.setText("Finished trip: " + tripName);
                }
            });
            tripViewModel.getStartLocation(tripIdFromNavigation).observe(getViewLifecycleOwner(), startLocation ->{
                this.startLocation = startLocation;
            });
            /*tripViewModel.getActualStartLocation(tripIdFromNavigation).observe(getViewLifecycleOwner(), actualStartLocation ->{
                this.actualStartLocation = actualStartLocation;
            });*/
            tripViewModel.getLocationsForTrip(tripIdFromNavigation).observe(getViewLifecycleOwner(), locations -> {
                drawTracks(locations);
            });
        }
    }

    private void setTripDetails(){
        tvTripDialogName.setText(trip.tripName);
        tvTripDialogPlanned.setText(trip.date);
        tvTripDialogStatus.setText(trip.status + "");
        tvTripDialogStart.setText(trip.startTime);
        tvTripDialogFinish.setText(trip.endTime);
        tvTripDialogDistance.setText(String.format("%,.2f km", trip.length/100));
        tvTripDialogDuration.setText(trip.duration/3600 + ":" + ((trip.duration/60)-((trip.duration/3600)*60)) +
                ":" + trip.duration%60);
        tvTripDialogToughness.setText(trip.getToughnessInText());
        tvTripDialogPace.setText(String.format("%,.2f m/s", trip.pace));
        tvTripDialogBackpack.setText(String.format("%,.1f kg", trip.extraLoad));
        tvTripDialogCalories.setText(String.format("%,.2f kcal", trip.calories));
        tvTripDialogElevation.setText(String.format("%,.2f m", trip.elevation));
        btInfo.setVisibility(View.VISIBLE);
    }

    public void drawTracks(List<Location> locations){
        for(int i = 0; i < locations.size(); i++){
            GeoPoint gp = new GeoPoint(locations.get(i).latitude, locations.get(i).longitude, locations.get(i).altitude);
            if(locations.get(i).partOfTrip == 0 || locations.get(i).partOfTrip == 1 || locations.get(i).partOfTrip == 2){
                if(locations.get(i).partOfTrip == 0){
                    startMarker.setPosition(gp);
                    map_view.getOverlays().add(startMarker);
                } else if (locations.get(i).partOfTrip == 1){
                    Marker waypointsMarker = new Marker(map_view);
                    waypointsMarker.setPosition(gp);
                    waypointsMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    map_view.getOverlays().add(waypointsMarker);
                    waypointsMarker.setIcon(getResources().getDrawable(R.drawable.ic_waypoint, null));
                    waypointsMarker.setTitle("Waypoint");
                } else {
                    endMarker.setPosition(gp);
                    map_view.getOverlays().add(endMarker);
                }
                if(!plannedRoute.getActualPoints().contains(gp)){
                    plannedRoute.addPoint(gp);
                }
            }

            if(locations.get(i).partOfTrip==3){
                if (!mPolyline.getActualPoints().contains(gp)){
                    if(i == locations.size()-1){
                        Marker endMarkTracked = new Marker(map_view);
                        endMarkTracked.setPosition(gp);
                        endMarkTracked.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        map_view.getOverlays().add(endMarkTracked);
                        endMarkTracked.setIcon(getResources().getDrawable(R.drawable.ic_flag, null));
                        endMarkTracked.setTitle("Actual endpoint.");
                    }
                    mPolyline.addPoint(gp);
                }
            }
            map_view.invalidate();
        }
    }

    private void createDialogForTripDetails(){
        tripDialog = new AlertDialog.Builder(requireContext()).create();
        tripDialog.setCancelable(false);
        tvTripDialogName = tripDialogView.findViewById(R.id.tvTripDetailName);
        tvTripDialogStatus = tripDialogView.findViewById(R.id.tvTripDetailStatusValue);
        tvTripDialogStart = tripDialogView.findViewById(R.id.tvTripDetailStartValue);
        tvTripDialogFinish = tripDialogView.findViewById(R.id.tvTripDetailFinishValue);
        tvTripDialogDistance = tripDialogView.findViewById(R.id.tvTripDetailLengthValue);
        tvTripDialogDuration = tripDialogView.findViewById(R.id.tvTripDetailDurationValue);
        tvTripDialogToughness = tripDialogView.findViewById(R.id.tvTripDetailToughnessValue);
        tvTripDialogPace = tripDialogView.findViewById(R.id.tvTripDetailPaceValue);
        tvTripDialogPlanned = tripDialogView.findViewById(R.id.tvTripDetailPlannedValue);
        tvTripDialogBackpack = tripDialogView.findViewById(R.id.tvTripDetailBackpackValue);
        tvTripDialogCalories = tripDialogView.findViewById(R.id.tvTripDetailCaloriesValue);
        tvTripDialogElevation = tripDialogView.findViewById(R.id.tvTripDetailElevationValue);
        tripDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tripDialog.dismiss();
            }
        });
        tripDialog.setView(tripDialogView);
    }

    private void createDialogForNewTrip(){
        alertDialog = new AlertDialog.Builder(requireContext()).create();
        alertDialog.setTitle("New Trip");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Fill in the name, optional backpack weight and date of trip ");

        etTripNameDialog = dialogView.findViewById(R.id.etTripNameDialog);
        etTripBackPack = dialogView.findViewById(R.id.etTripBackPackDialog);
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
                double backPackWeight = Double.parseDouble(etTripBackPack.getText().toString());
                tripName = etTripNameDialog.getText().toString();
                tripViewModel.getNewTrip().observe(getViewLifecycleOwner(), newTrip ->{
                    trip = newTrip;
                    setTripDetails();
                    //btInfo.setVisibility(View.VISIBLE);
                });
                if(!tripName.equals("") && date != null){
                    String dateString = new SimpleDateFormat(MY_DATE_FORMAT).format(date.getTime());
                    Trip inputTrip = new Trip(tripName, dateString, backPackWeight);
                    tripViewModel.insert(inputTrip);
                    tvTripNameMap.setText(inputTrip.tripName);
                    btAdd.setVisibility(View.GONE);
                    btPlanRoute.setVisibility(View.VISIBLE);
                    registerMode = true;
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

        addSingleTapOnPlanning();
    }

    private void addSingleTapOnPlanning() {
        final MapEventsReceiver planMapReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                if (planRoute){
                    if(startMarker.isDisplayed()){
                        chosenStart = true;
                    }
                    if(endMarker.isDisplayed()){
                        chosenEnd = true;
                    }
                    if (!chosenStart && !chosenEnd && tripStatusFromNavigation == 0 && tripIdFromNavigation == 0) {
                        startMarker.setPosition(geoPoint);
                        map_view.getOverlays().add(startMarker);
                        Location startLocation1 = new Location(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude(), trip.tripId, 0);
                        startLocation=startLocation1;
                        tripViewModel.insert(startLocation1);
                        tvTripNameMap.setText("LOCATE THE ENDPOINT for " + trip.tripName);
                    } else if (chosenStart && !chosenEnd && tripStatusFromNavigation == 0 && tripIdFromNavigation == 0) {
                        endMarker.setPosition(geoPoint);
                        map_view.getOverlays().add(endMarker);
                        Location endLocation = new Location(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude(), trip.tripId, 2);
                        tripViewModel.insert(endLocation);
                        tvTripNameMap.setText("Locate waypoints or start tracking." + trip.tripName);
                        trip.status = 1;
                        tripViewModel.updateTrip(trip);
                        btPlay.setVisibility(View.VISIBLE);
                        btSave.setVisibility(View.VISIBLE);
                    } else { //mellompunkter
                        Marker waypointsMarker = new Marker(map_view);
                        waypointsMarker.setPosition(geoPoint);
                        waypointsMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        map_view.getOverlays().add(waypointsMarker);
                        waypointsMarker.setIcon(getResources().getDrawable(R.drawable.ic_waypoint, null));
                        waypointsMarker.setTitle("Waypoint");
                        Location wayPoint = new Location(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude(), trip.tripId, 1);
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
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            requestingLocationUpdates = true;
                            requestLocationUpdates();
                            initTripObserver();
                        }  else {
                            showMapOnly = true;
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
            if(!showMapOnly){
                permissionDialog.show();
                //locationPermissionRequest.launch(requiredLocationPermissions);
            } else {
                Toast.makeText(requireContext(), "Location updates is off and Tracking will only show the map.", Toast.LENGTH_SHORT).show();
                btAdd.setVisibility(View.GONE);
                btAutoCenter.setVisibility(View.GONE);
                btPlay.setVisibility(View.GONE);
            }
        } else {
            requestingLocationUpdates = true;
            requestLocationUpdates();
            initTripObserver();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tripStatusFromNavigation == 0 && trip != null && trip.status ==0 && startLocation!=null){
            tripViewModel.deleteLocationsForTrip(trip.tripId);
        }
        if (tripStatusFromNavigation == 0 && trip != null && trip.status == 1){
            Toast.makeText(requireContext(), "The trip " + trip.tripName + " is saved to planned trips.", Toast.LENGTH_SHORT).show();
        }
        Log.d("CURRENT", "onPause");
        mLocationManager.removeUpdates(this);
        tripViewModel.deleteTripsToBeRegistered();
        tripViewModel.deleteOngoingTrips();
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        if(requestingLocationUpdates){
            GeoPoint gp = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
            if(currentLocation== null){
                previousLocation = location;
            } else {
                previousLocation = currentLocation;
            }
            currentLocation = location;
            if (currentPositionMarker != null) {
                currentPositionMarker.setPosition(gp);
                currentPositionMarker.setTitle(location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude());
                map_view.getOverlays().add(currentPositionMarker);
            }
            if (tracking){
                runTracking(gp);
                if(trip!=null && trip.status == 2){
                    tripViewModel.insert(new Location(location.getLatitude(), location.getLongitude(), location.getAltitude(), trip.tripId, 3));
                }
            }
            if (autoCentering){
                map_view.getController().setCenter(gp);
            }
        }
    }

    private void createDialogForLocationRequirement(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Location dependent application");
        builder.setCancelable(false);
        builder.setMessage("This application requires location updates to be able to show your position on the map and track your movement. " +
                "You can still be able to show the map without the location updates.");

        builder.setNegativeButton("Understood", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                locationPermissionRequest.launch(requiredLocationPermissions);
            }
        });

       permissionDialog = builder.create();

    }

    private void savePersonData(){
        try{
            FileOutputStream f = new FileOutputStream(file, false);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(user);
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error initializing stream", Toast.LENGTH_SHORT).show();
        }
    }

    private void readPersonData(){
        try{
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);
            user = (Person) o.readObject();
            //setPersonData();
            o.close();
            f.close();
        } catch (FileNotFoundException e){
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error initializing stream", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e){
            Toast.makeText(requireContext(), "Class not found", Toast.LENGTH_SHORT).show();
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

}