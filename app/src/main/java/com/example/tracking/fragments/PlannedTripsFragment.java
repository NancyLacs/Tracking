package com.example.tracking.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.tracking.entities.Trip;
import com.example.tracking.viewmodel.TripViewModel;

public class PlannedTripsFragment extends Fragment {

    private TripViewModel tripViewModel;
    private RecyclerView rvTrip;
    private TripsAdapter tripsAdapter;
    private int tripStatusFromAction, tripStatusFromViewModel;
    private TextView tvTitle;
    private AlertDialog tripDialog;
    private View tripDialogView;
    private TextView tvTripDialogName, tvTripDialogStatus, tvTripDialogStart, tvTripDialogFinish, tvTripDialogDistance,
            tvTripDialogDuration, tvTripDialogToughness, tvTripDialogPace, tvTripDialogPlanned;



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
        //super.onViewCreated(view, savedInstanceState);
        tripDialogView = getLayoutInflater().inflate(R.layout.trip_detail_layout, null);
        tvTitle = view.findViewById(R.id.tvPlannedTrips);
        rvTrip = view.findViewById(R.id.rvPlannedTrips);
        createDialogForTripDetails();
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
                            case R.id.ivTripInfo:
                                setTripDetails(plannedTrips.get(position));
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
                            case R.id.ivTripInfo:
                                setTripDetails(finishedTrips.get(position));
                                break;
                        }
                    }
                });
            });
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

        tripDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tripDialog.dismiss();
            }
        });
        tripDialog.setView(tripDialogView);
    }
    private void setTripDetails(Trip trip){
        tvTripDialogName.setText(trip.tripName);
        tvTripDialogPlanned.setText(trip.date);
        tvTripDialogStatus.setText(trip.status + "");
        tvTripDialogStart.setText(trip.startTime);
        tvTripDialogFinish.setText(trip.endTime);
        tvTripDialogDistance.setText(String.format("%,.2f km", trip.length/100));
        tvTripDialogDuration.setText(trip.duration + "");
        tvTripDialogToughness.setText(trip.getToughnessInText());
        tvTripDialogPace.setText(String.format("%,.2f m/s", trip.pace));
        tripDialog.show();
    }
}