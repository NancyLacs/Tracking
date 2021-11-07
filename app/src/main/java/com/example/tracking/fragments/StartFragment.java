package com.example.tracking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tracking.R;


public class StartFragment extends Fragment {

    private Button btPlanned;
    private Button btFinished;
    private Button btNewTrip;

    public StartFragment() {
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
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btPlanned = view.findViewById(R.id.btPlanned);
        btFinished = view.findViewById(R.id.btFinished);
        btNewTrip = view.findViewById(R.id.btNewTrip);
        NavController navController = Navigation.findNavController(view);
        btPlanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = StartFragmentDirections.actionStartFragmentToPlannedTripsFragment();
                navController.navigate(action);
            }
        });

        btFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = StartFragmentDirections.actionStartFragmentToFinishedTripsFragment();
                navController.navigate(action);
            }
        });
        btNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = StartFragmentDirections.actionStartFragmentToNewTripFragment();
                navController.navigate(action);
            }
        });
    }
}