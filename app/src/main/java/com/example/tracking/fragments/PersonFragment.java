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
import android.widget.EditText;
import android.widget.Toast;

import com.example.tracking.R;
import com.example.tracking.entities.Person;
import com.example.tracking.entities.Trip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {

    private final String PERSONFILE = "user.txt";
    private Person user;
    private File file;
    private Button btnSave, btnContinue;
    private View btnSaveView;
    private MenuItem home;
    private EditText etName, etAge, etWeight, etDistance, etAvgToughness, etTotalCalories, etAvgPace, etTotalTrips, etTotalSteps;


    public PersonFragment() {
        // Required empty public constructor
    }


    public static PersonFragment newInstance() {
        PersonFragment fragment = new PersonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnSave = view.findViewById(R.id.btProfileSave);
        btnContinue = view.findViewById(R.id.btProfileContinue);
        etName = view.findViewById(R.id.etProfileName);
        etAge = view.findViewById(R.id.etProfileAge);
        etWeight = view.findViewById(R.id.etProfileWeight);
        etDistance = view.findViewById(R.id.etProfileDistanceHiked);
        etAvgToughness = view.findViewById(R.id.etProfileAverageToughness);
        etTotalCalories = view.findViewById(R.id.etProfileCalories);
        etAvgPace = view.findViewById(R.id.etProfileAveragePace);
        etTotalSteps = view.findViewById(R.id.etProfileTotalSteps);
        etTotalTrips = view.findViewById(R.id.etProfileNrOfTrips);
        String path = requireContext().getFilesDir().getAbsolutePath();
        file = new File(path, PERSONFILE);
        if(file.exists()){
            readPersonData();
            btnContinue.setVisibility(View.VISIBLE);
        } else {
            btnContinue.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Please enter your profile information to continue.", Toast.LENGTH_SHORT).show();
        }

        btnContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavDirections action = PersonFragmentDirections.actionPersonFragmentToStartFragment();
                navController.navigate(action);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveView = v;
                if(!file.exists()){
                    String name = etName.getText().toString();
                    String age = etAge.getText().toString();
                    String weight = etWeight.getText().toString();
                    if(name.equals("") || age.equals("")|| weight.equals("")){
                        Toast.makeText(requireContext(), "Please enter your profile information.", Toast.LENGTH_SHORT).show();
                    } else {
                        user = new Person(name, Integer.parseInt(age), Double.parseDouble(weight));
                        try {
                            if(file.createNewFile()){
                                savePersonData();
                            }
                        } catch (IOException e) {
                            Toast.makeText(requireContext(), "Error in creating file!", Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(requireContext(), name + ", " + age + ", " + weight, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    updatePersonData();
                }

            }
        });
    }

    /*@Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        home = menu.findItem(R.id.welcomeFragment);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    private void savePersonData(){
        try{
            FileOutputStream f = new FileOutputStream(file, false);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(user);
            o.close();
            f.close();
            /*NavController navController = Navigation.findNavController(btnSaveView);
            NavDirections action = PersonFragmentDirections.actionPersonFragmentToStartFragment();
            navController.navigate(action);*/
            btnContinue.setVisibility(View.VISIBLE);
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
            setPersonData();
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

    private void setPersonData(){
        etName.setText(user.getName());
        etAge.setText(user.getAge() + "");
        etWeight.setText(user.getWeight() +"");
        etDistance.setText(String.format("%,.2f", user.getDistanceHiked()/1000));
        etAvgToughness.setText(Trip.getToughnessInText(user.getAverageToughness()));
        etTotalCalories.setText(String.format("%,.2f", user.getTotalCalories()));
        etAvgPace.setText(user.getAveragePace() + "");
        etTotalSteps.setText(user.getTotalSteps() + "");
        etTotalTrips.setText(user.getNrOfTrips() + "");
    }

    private void updatePersonData(){
        user.setName(etName.getText().toString());
        user.setAge(Integer.parseInt(etAge.getText().toString()));
        user.setWeight(Double.parseDouble(etWeight.getText().toString()));
        /*user.setDistanceHiked(Double.parseDouble(etDistance.getText().toString()));
        user.setAverageToughness(Double.parseDouble(etAvgToughness.getText().toString()));
        user.setTotalCalories(Double.parseDouble(etTotalCalories.getText().toString()));
        user.setAveragePace(Double.parseDouble(etAvgPace.getText().toString()));
        user.setTotalSteps(Integer.parseInt(etTotalSteps.getText().toString()));*/
        savePersonData();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem person= menu.findItem(R.id.profileFragment);
        person.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}