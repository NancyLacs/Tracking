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
import android.widget.EditText;
import android.widget.Toast;

import com.example.tracking.R;
import com.example.tracking.entities.Person;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {

    private final String PERSONFILE = "personFile.txt";
    private Person user;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnSave = view.findViewById(R.id.btProfileSave);
        Button btnContinue = view.findViewById(R.id.btProfileContinue);
        EditText etName = view.findViewById(R.id.etProfileName);
        EditText etAge = view.findViewById(R.id.etProfileAge);
        EditText etWeight = view.findViewById(R.id.etProfileWeight);
        File file = new File(PERSONFILE);
        if(file.exists()){
            btnContinue.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "File exists.", Toast.LENGTH_SHORT).show();
        } else {
            btnContinue.setVisibility(View.GONE);
            //Toast.makeText(this, "File does not exist.", Toast.LENGTH_SHORT).show();
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
            public void onClick(View view) {
                if(!file.exists()){
                    String name = etName.getText().toString();
                    String age = etAge.getText().toString();
                    String weight = etWeight.getText().toString();
                    if(name.equals("") || age.equals("")|| weight.equals("")){
                        Toast.makeText(requireContext(), "Please enter your profile information.", Toast.LENGTH_SHORT).show();
                    } else {
                        //user = new Person(name, Integer.parseInt(age), Double.parseDouble(weight));
                        //savePersonData();
                        Toast.makeText(requireContext(), name + ", " + age + ", " + weight, Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });
    }

    private void savePersonData(){

    }

}