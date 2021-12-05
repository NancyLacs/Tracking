package com.example.tracking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tracking.R;

import java.io.File;


public class StartFragment extends Fragment {

    private Button btPlanned;
    private Button btFinished;
    private Button btMapFrag;
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
        btMapFrag = view.findViewById(R.id.btMapFrag);
       //NavController navController = Navigation.findNavController(view);
        btPlanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                StartFragmentDirections.ActionStartFragmentToPlannedTripsFragment action = StartFragmentDirections.actionStartFragmentToPlannedTripsFragment();
                action.setTripStatus(1);
                navController.navigate(action);
            }
        });

        btFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                StartFragmentDirections.ActionStartFragmentToPlannedTripsFragment action = StartFragmentDirections.actionStartFragmentToPlannedTripsFragment();
                action.setTripStatus(3);
                navController.navigate(action);
            }
        });

        btMapFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavDirections action = StartFragmentDirections.actionStartFragmentToMapFragment();
                navController.navigate(action);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem home = menu.findItem(R.id.welcomeFragment);
        File file = new File("personFile.txt");
        if(file.exists()){
            home.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}