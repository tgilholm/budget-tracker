package com.example.budgettracker.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.budgettracker.R;
import com.example.budgettracker.utility.DatePickerFragment;
import com.example.budgettracker.utility.TimePickerFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Locale;


/**
 * The fragment subclass for the Add section of the app
 * Connects to fragment_add.xml to provide layout
 */

public class AddFragment extends Fragment {
    private EditText dateText;
    private EditText timeText;

    /*
    TODO: Add Functionality- save new transaction to persistent storage, update all screens
    If connected to cloud, save to cloud.
     */
    /*
    TODO: Chip group containing chips representing each category- default categories & user defined
     */

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

    // Creates the layout and event listeners for the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        /* ONCLICK LISTENERS */

        // Connect an onClickListener to the date and time fields
        dateText = v.findViewById(R.id.editTextDate);
        dateText.setOnClickListener(this::onDatePressed);

        timeText = v.findViewById(R.id.editTextTime);
        timeText.setOnClickListener(this::onTimePressed);

        FloatingActionButton fab = v.findViewById(R.id.addButton);


        /* FRAGMENT RESULT LISTENERS */
        /*
        Connects the date and time picker fragments to the parent fragment manager
        onFragmentResult is used to take the bundled date or time from the fragment
        and pass it to AddFragment
         */
        getParentFragmentManager().setFragmentResultListener("dateKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String result = bundle.getString("dateKey");
                dateText.setText(result);   // Update the dateText field with the requested date
            }
        });

        getParentFragmentManager().setFragmentResultListener("timeKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String result = bundle.getString("timeKey");
                timeText.setText(result);   // Update the timeText field with the requested date
            }
        });

        /* STARTUP LOGIC */
        /*
        - Sets default values for the date and time fields
         */

        // Set the date and time fields to the current date & time
        final Calendar c = Calendar.getInstance(); // Retrieve the current time
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Month is indexed from 0, so add 1 to prevent off by ones
        String currentDate = day + "/" + (month + 1) + "/" + year;
        dateText.setText(currentDate);

        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // Always use 00:00 format for time output, i.e. 03:00 instead of 3.0
        // Set the locale to be the user's region
        String currentTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        timeText.setText(currentTime);



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
        // Highlight the date text
        dateText.selectAll();

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getParentFragmentManager(), "datePicker");
    }

    // Open a TimePickerDialog when the user interacts with the date field
    public void onTimePressed(View view)
    {
        timeText.selectAll();
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.show(getParentFragmentManager(), "timePicker");
    }

}