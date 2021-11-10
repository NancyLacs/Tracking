package com.example.tracking.fragments;

import static com.example.tracking.db.TripLocationRoomDB.simpleDateFormat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tracking.R;
import com.example.tracking.entities.Trip;
import com.example.tracking.viewmodel.TripViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NewTripFragment extends Fragment {

    private Button btNewTrip;
    private TripViewModel tripViewModel;
    private EditText etTripName;
    private CalendarView cvTripDate;
    private Calendar date = null;
    public static final String MY_DATE_FORMAT = "dd.MM.yyyy";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MY_DATE_FORMAT);
    private String selectedDate, tripName, newDate;
    private Trip trip;

    //private NavController navController;

    public NewTripFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btNewTrip = view.findViewById(R.id.btSaveNewTrip);
        etTripName = view.findViewById(R.id.etTripName);
        cvTripDate = view.findViewById(R.id.cvTripDate);
        newDate = NewTripFragmentArgs.fromBundle(getArguments()).getNewDate();
        tripName = NewTripFragmentArgs.fromBundle(getArguments()).getNewTripName();

        //navController = Navigation.findNavController(view);
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        /*tripViewModel.getNewSpecificTrip(tripName, newDate).observe(getViewLifecycleOwner(), trip ->{
            this.trip = trip;
        });*/

        tripViewModel.getTripById(54).observe(getViewLifecycleOwner(), trip1 -> {
            trip = trip1;
        });

        etTripName.setText(trip.tripName);


        cvTripDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "." + (month+1) + "." + year;
                try{
                    date = Calendar.getInstance();
                    date.setTime(simpleDateFormat.parse(selectedDate));
                }catch (ParseException e){
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "ParseException: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });


        btNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripName = etTripName.getText().toString();
                if(!tripName.equals("") && date != null){
                    //NewTripFragmentDirections.ActionNewTripFragmentToMapFragment action = NewTripFragmentDirections.actionNewTripFragmentToMapFragment();
                    String dateString = new SimpleDateFormat(MY_DATE_FORMAT).format(date.getTime());
                    Trip trip = new Trip(tripName, dateString);
                    long tripId = tripViewModel.insert(trip);
                    trip.status = 1; //under registering
                    tripViewModel.updateTrip(trip);
                    //action.setTripId(0);
                    //action.setTripStatus(1);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.mapFragment);
                } else if(!tripName.equals("") && date == null){
                    Toast.makeText(requireContext(), "You must choose a date.", Toast.LENGTH_SHORT).show();
                } else if(tripName.equals("") && date != null){
                    Toast.makeText(requireContext(), "You must provide a trip name.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "You must fill in the necessary information.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}