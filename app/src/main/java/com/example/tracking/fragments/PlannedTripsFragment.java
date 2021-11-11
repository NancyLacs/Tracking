package com.example.tracking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tracking.R;
import com.example.tracking.adapter.TripsAdapter;
import com.example.tracking.viewmodel.TripViewModel;

public class PlannedTripsFragment extends Fragment {

    private TripViewModel tripViewModel;
    private RecyclerView rvTrip;
    private TripsAdapter tripsAdapter;
    private int tripStatusFromAction, tripStatusFromViewModel;
    private TextView tvTitle;



    public PlannedTripsFragment() {
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
        return inflater.inflate(R.layout.fragment_planned_trips, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = view.findViewById(R.id.tvPlannedTrips);
        rvTrip = view.findViewById(R.id.rvPlannedTrips);

        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tripStatusFromAction = PlannedTripsFragmentArgs.fromBundle(getArguments()).getTripStatus();
        if (tripStatusFromAction == 1){
            tvTitle.setText("Planned Trips");
            tripViewModel.getPlannedTrips().observe(getViewLifecycleOwner(), plannedTrips -> {
                tripsAdapter = new TripsAdapter(requireContext(), plannedTrips);
                rvTrip.setAdapter(tripsAdapter);
                rvTrip.setLayoutManager(new LinearLayoutManager(getContext()));
                tripsAdapter.setClickListener(new TripsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        switch (v.getId()){
                            case R.id.tvTripNameInList:
                                NavController navController = Navigation.findNavController(v);
                                PlannedTripsFragmentDirections.ActionPlannedTripsFragmentToMapFragment action = PlannedTripsFragmentDirections.actionPlannedTripsFragmentToMapFragment();
                                long tripId = plannedTrips.get(position).getTripId();
                                int tripStatus = plannedTrips.get(position).getStatus();
                                action.setTripStatus(tripStatus);
                                action.setTripId(tripId);
                                navController.navigate(action);
                                break;
                            case R.id.ivDeleteTrip:
                                tripViewModel.deleteTrip(plannedTrips.get(position));
                                break;
                        }
                    }
                });
            });
        }
        if (tripStatusFromAction == 3){
            tvTitle.setText("Finished Trips");
            tripViewModel.getAllFinishedTrips().observe(getViewLifecycleOwner(), finishedTrips -> {
                tripsAdapter = new TripsAdapter(requireContext(), finishedTrips);
                rvTrip.setAdapter(tripsAdapter);
                rvTrip.setLayoutManager(new LinearLayoutManager(getContext()));
                tripsAdapter.setClickListener(new TripsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        switch (v.getId()){
                            case R.id.tvTripNameInList:
                                NavController navController = Navigation.findNavController(v);
                                PlannedTripsFragmentDirections.ActionPlannedTripsFragmentToMapFragment action = PlannedTripsFragmentDirections.actionPlannedTripsFragmentToMapFragment();
                                long tripId = finishedTrips.get(position).getTripId();
                                int tripStatus = finishedTrips.get(position).getStatus();
                                action.setTripStatus(tripStatus);
                                action.setTripId(tripId);
                                navController.navigate(action);
                                break;
                            case R.id.ivDeleteTrip:
                                tripViewModel.deleteTrip(finishedTrips.get(position));
                                break;
                        }
                    }
                });
            });
        }
    }
}