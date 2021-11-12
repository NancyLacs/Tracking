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


}