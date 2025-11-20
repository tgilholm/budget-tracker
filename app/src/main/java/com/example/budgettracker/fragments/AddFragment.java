package com.example.budgettracker.fragments;


import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.budgettracker.R;
import com.example.budgettracker.Transaction;
import com.example.budgettracker.TransactionViewModel;
import com.example.budgettracker.timeselector.DatePickerFragment;
import com.example.budgettracker.utility.ColorHandler;
import com.example.budgettracker.utility.InputValidator;
import com.example.budgettracker.timeselector.TimePickerFragment;
import com.example.budgettracker.enums.RepeatDuration;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.utility.Converters;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * The fragment subclass for the Add section of the app
 * Connects to fragment_add.xml to provide layout
 */

public class AddFragment extends Fragment
{
    private EditText dateText;
    private EditText timeText;
    private ChipGroup chipGroupCategories;


    private TransactionViewModel transactionViewModel;

    // Update UI to be more like Google Calendar
    // Dropdown menu for time and date & keep both selected
    /*
    TODO: Add Functionality- save new transaction to persistent storage, update all screens
    If connected to cloud, save to cloud.
     */
    /*
    TODO: Chip group containing chips representing each category- default categories & user defined
     */

    public AddFragment()
    {
    }

    // Handle the logic for the fragment startup
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Connect the TransactionViewModel
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);



    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    // Creates the layout and event listeners for the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Instantiate a new View with the inflated XML layout
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        // VIEW IDs
        FloatingActionButton addButton = v.findViewById(R.id.addButton);
        timeText = v.findViewById(R.id.editTextTime);
        dateText = v.findViewById(R.id.editTextDate);


        // ONCLICK LISTENERS
        // Connect an onClickListener to the date and time fields
        dateText.setOnClickListener(this::onDatePressed);
        timeText.setOnClickListener(this::onTimePressed);

        // Connect an onClickListener the add button
        addButton.setOnClickListener(this::onAddPressed);


        // FRAGMENT RESULT LISTENERS
        /*
        Connects the date and time picker fragments to the parent fragment manager
        onFragmentResult is used to take the bundled date or time from the fragment
        and pass it to AddFragment
         */
        getParentFragmentManager().setFragmentResultListener("dateKey", this, (requestKey, bundle) ->
        {
            String result = bundle.getString("dateKey");
            dateText.setText(result);   // Update the dateText field with the requested date
        });

        getParentFragmentManager().setFragmentResultListener("timeKey", this, (requestKey, bundle) ->
        {
            String result = bundle.getString("timeKey");
            timeText.setText(result);   // Update the timeText field with the requested date
        });

        // STARTUP LOGIC
        setInitialDateTime();   // Sets default values for the date and time fields

        // Get the chip group for the categories from the layout and populate with the existing categories
        chipGroupCategories = v.findViewById(R.id.chipGroupCategories);
        populateChipGroup();    // Adds the categories to the chip group

        return v;
    }

    // Populate the ChipGroup with the pre-set categories //TODO MOVE TO DATABASE
    private void populateChipGroup()
    {
        // Create a default list of categories
        List<String> categories = Arrays.asList("Entertainment", "Petrol", "Pets", "Travel", "Shopping");
        List<Integer> colorIDs = Arrays.asList(R.color.lightGreen, R.color.lightBlue, R.color.lightRed, R.color.lightYellow, R.color.lightPurple);


        for (String i : categories)
        {
            // Resolve the color IDs from colors.xml to a color integer before passing to Chip
            int colorID = colorIDs.get(categories.indexOf(i));
            //int resolvedColor = ContextCompat.getColor(requireContext(), colorID);

            Chip chip = createChip(i, ColorHandler.resolveColorID(getContext(), colorID));
            chipGroupCategories.addView(chip); // Add the new chip group to the view
        }
    }

    // Allows for the creation of default and user-defined category chips //TODO Add color picker
    @NonNull
    private Chip createChip(String label, ColorStateList color)
    {
        Chip newChip = new Chip(getContext(), null, R.style.Theme_BudgetTracker_ChipStyle);

        // Set the name of the chip
        newChip.setText(label);
        newChip.setCheckable(true);
        newChip.setClickable(true);

        // Set the background color of the chip
        newChip.setChipBackgroundColor(color);
        return newChip;
    }


    // Open a DatePickerDialog when the user interacts with the date field
    public void onDatePressed(View view)
    {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getParentFragmentManager(), "datePicker");

    }

    // Open a TimePickerDialog when the user interacts with the date field
    public void onTimePressed(View view)
    {
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.show(getParentFragmentManager(), "timePicker");
    }

    // Sets the DateText and TimeText fields to the current time
    private void setInitialDateTime()
    {
        Calendar userTime = Calendar.getInstance();

        // Always use 00:00 format for time output, i.e. 03:00 instead of 3.0
        // Set the locale to be the user's region
        timeText.setText(Converters.calendarToHourMinute(userTime));

        dateText.setText(Converters.calendarToDayMonthYear(userTime));

    }


    // Handle the logic for the add button
    // Collects all the user input into a new transaction
    // TODO set character limits on fields
    // TODO clear all fields on add
    public void onAddPressed(View view)
    {
        // Parcelable order:
        /*
            - id
            - amount
            - type
            - date
            - category
            - repeat
         */
        // Bundle all the user input into a new transaction
        double amount = getAmount();        // Get the transaction amount
        if (amount < 0) { return; }         // Break here if getAmount() failed

        TransactionType type = getType();   // Get the transaction type

        Calendar dateTime = getDateAndTime();
        if (dateTime == null) {return;}     // Break here if getDateAndTime() failed

        String category = getCategory();    // Get the transaction category
        if (category == null) {return;}     // Break here if getCategory() failed

        RepeatDuration repeatDuration = getRepeatDuration();

        Log.v("AddFragment", "Adding new transaction");

        // Add the transaction to the list of transactions
        Transaction newTransaction = new Transaction(amount, type, dateTime, category, repeatDuration);
        transactionViewModel.addTransaction(newTransaction);
    }


    // Get the transaction amount inputted by the user
    private double getAmount()
    {
        // Get the amount from the amountText field
        EditText amountText = requireView().findViewById(R.id.editTextAmount);
        String amount = amountText.getText().toString().trim(); // Remove any whitespace

        // Validate the input
        return InputValidator.validateCurrencyInput(getContext(), amount);
    }

    // Return the type of the transaction- incoming or outgoing
    private TransactionType getType()
    {
        // Get the type radio buttons from the layout
        RadioButton rbIncoming = requireView().findViewById(R.id.rbIncoming);

        // Only one radio button can be selected at a time
        if (rbIncoming.isChecked())
        {
            return TransactionType.INCOMING;
        } else
        {
            return TransactionType.OUTGOING;
        }
    }

    // Return the category of the transaction
    @Nullable
    private String getCategory()
    {
        // The ChipGroup uses Single Selection mode, making the process of finding the checked chip faster
        int selectedChipId = chipGroupCategories.getCheckedChipId();

        // If no chip is selected, inform the user via a toast message
        if (selectedChipId == View.NO_ID) // View.NO_ID has a value of -1
        {
            Toast.makeText(getContext(), "No Category Selected!", Toast.LENGTH_SHORT).show();
            return null; // Return null if no chip is selected
        } else
        {
            // Returns the chip with the ID of the selected chip
            Chip selectedCategory = chipGroupCategories.findViewById(selectedChipId);
            return selectedCategory.getText().toString(); // Returns the text of the chip
        }
    }

    // Gets the date and time in the dateText and timeText fields and returns them as a Calendar object
    // Storing the values as a Calendar helps with operations elsewhere in the program
    private Calendar getDateAndTime()
    {
        // Get the values from the EditText fields
        String date = dateText.getText().toString();
        String time = timeText.getText().toString();

        // Pass the date and time values to the validateDateAndTime method
        return InputValidator.validateDateTimeInput(getContext(), date + " " + time);
    }

    private RepeatDuration getRepeatDuration()
    {
        // Get the radio group from the layout
        RadioGroup radioGroupSelectRepeat = requireView().findViewById(R.id.radioGroupSelectRepeat);
        int selectedRadioButtonID = radioGroupSelectRepeat.getCheckedRadioButtonId();

        // Get the selected RadioButton by indexing the radio group
        RadioButton rbSelected = radioGroupSelectRepeat.findViewById(selectedRadioButtonID);

        // Pass the rbSelected field to the selectRepeatDuration method in InputValidator
        return InputValidator.selectRepeatDuration(rbSelected.getText().toString());
    }
}