package com.example.budgettracker.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.budgettracker.R;
import com.example.budgettracker.utility.DatePickerFragment;


/**
 * The fragment subclass for the Add section of the app
 * Connects to fragment_add.xml to provide layout
 */

public class AddFragment extends Fragment {
    //private FragmentManager fragmentManager;
    /*
    TODO: Add Functionality- save new transaction to persistent storage, update all screens
    If connected to cloud, save to cloud.
     */
    /*
    TODO: Chip group containing chips representing each category- default categories & user defined
     */

    // TODO DialogFragment displaying information to input date and time of transaction
    // By default today's date is already inputted whenever the fragment is loaded
    // The user can then tap on the date or the time and it will open a popup prompting them to change it

    public AddFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    // TODO change the Date and TimePickerFragments to be self contained and send data to a parent receiver
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        // Connect an onClickListener to the date field
        final EditText dateText = v.findViewById(R.id.editTextDate);
        dateText.setOnClickListener(this::onDatePressed);

        final Button button = v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getParentFragmentManager(), "t");
            }

        });

        // Create a FragmentManager to handle the date & time pickers
        //fragmentManager = requireActivity().getSupportFragmentManager();

        // Set the current date & time in the textEdit fields
        return v;
    }


    // Handle the logic for the fragment startup
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    // Open a DatePickerDialog when the user interacts with the date field
    public void onDatePressed(View view)
    {
        DatePickerFragment datePicker = new DatePickerFragment() {

            // Update the text field when the user has finished picking a date
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                System.out.print(hourOfDay + minute);
            }
        };
        datePicker.show(getParentFragmentManager(), "datePicker");
    }

}