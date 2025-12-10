package com.example.budgettracker.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.budgettracker.dialogs.CategoryCreatorFragment;
import com.example.budgettracker.entities.Category;
import com.example.budgettracker.viewmodel.AddViewModel;
import com.example.budgettracker.dialogs.DatePickerFragment;
import com.example.budgettracker.utility.ChipHandler;
import com.example.budgettracker.utility.InputValidator;
import com.example.budgettracker.dialogs.TimePickerFragment;
import com.example.budgettracker.enums.RepeatDuration;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.utility.Converters;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private AddViewModel addViewModel;


    // Creates the layout and event listeners for the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Instantiate a new View with the inflated XML layout
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        // Get IDs from the view
        FloatingActionButton addButton = v.findViewById(R.id.addButton);
        timeText = v.findViewById(R.id.editTextTime);
        dateText = v.findViewById(R.id.editTextDate);
        chipGroupCategories = v.findViewById(R.id.chipGroupCategories);


        // Connect the AddViewModel
        addViewModel = new ViewModelProvider(requireActivity()).get(AddViewModel.class);


        // ONCLICK LISTENERS
        dateText.setOnClickListener(this::onDatePressed);
        timeText.setOnClickListener(this::onTimePressed);
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


        // Set up the observer on the categories list
        // When new categories are added, refresh the category list
        addViewModel.getCategories().observe(getViewLifecycleOwner(), this::populateChipGroup);


        setInitialDateTime();   // Sets default values for the date and time fields
        return v;
    }

    // Populate the ChipGroup with categories
    private void populateChipGroup(@NonNull List<Category> categories)
    {
        // Replace the chips with new ones
        chipGroupCategories.removeAllViews();

        // Add the new chips
        for (Category c : categories)
        {
            chipGroupCategories.addView(ChipHandler.createChip(requireContext(), c));
        }

        // Create the "add category" chip
        Chip addChip = ChipHandler.createAddCategoryChip(requireContext());

        // Set the onClickListener for the chip
        addChip.setOnClickListener(v -> {
            // Open a DialogFragment with a Name and Colour picker

            showCategoryCreator();

        });

        chipGroupCategories.addView(addChip);
    }


    // Handle opening the category creator
    private void showCategoryCreator()
    {
        // Get the list of colours from the colors.xml file


        // Add the colours to the RecyclerView and set the adapter



        // Handle what happens when a colour is selected


        CategoryCreatorFragment categoryCreatorFragment = new CategoryCreatorFragment(requireContext());
        categoryCreatorFragment.show(getParentFragmentManager(), "categoryCreator");
    }


    // Open a DatePickerDialog when the user interacts with the date field
    public void onDatePressed(View view)
    {
        DatePickerFragment datePicker = new DatePickerFragment(getContext());
        datePicker.show(getParentFragmentManager(), "datePicker");
    }

    // Open a TimePickerDialog when the user interacts with the date field
    public void onTimePressed(View view)
    {
        TimePickerFragment timePicker = new TimePickerFragment(getContext());
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
    public void onAddPressed(View view)
    {
        // Transaction order:
        /*
            - id
            - amount
            - type
            - date
            - categoryID
            - repeat
         */
        // Bundle all the user input into a new transaction
        double amount = getAmount();        // Get the transaction amount
        if (amount < 0)
        {
            return;
        }         // Break here if getAmount() failed

        TransactionType type = getType();   // Get the transaction type

        Calendar dateTime = getDateAndTime();
        if (dateTime == null)
        {
            return;
        }     // Break here if getDateAndTime() failed

        long categoryID = getCategoryID();    // Get the transaction category
        if (categoryID < 0)
        {
            return;
        }     // Break here if getCategoryID() failed

        RepeatDuration repeatDuration = getRepeatDuration();

        Log.v("AddFragment", "Adding new transaction");

        // Send the transaction details to the ViewModel
        addViewModel.addTransaction(amount, type, dateTime, categoryID, repeatDuration);

        // Inform the user via a toast that the transaction was added
        Toast.makeText(getContext(), "Added new transaction!", Toast.LENGTH_SHORT).show();
    }


    // Get the transaction amount inputted by the user
    private double getAmount()
    {
        // Get the amount from the amountText field
        EditText amountText = requireView().findViewById(R.id.editTextAmount);
        String amount = amountText.getText().toString().trim(); // Remove any whitespace

        // Validate the input
        if (InputValidator.validateCurrencyInput(getContext(), amount))
        {
            return Double.parseDouble(amount);
        } else
        {
            Log.v("AddFragment", "Invalid amount input");
            return 0;
        }
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
    private long getCategoryID()
    {
        // The ChipGroup uses Single Selection mode, making the process of finding the checked chip faster
        int selectedChipId = chipGroupCategories.getCheckedChipId();

        // If no chip is selected, inform the user via a toast message
        if (selectedChipId == View.NO_ID) // View.NO_ID has a value of -1
        {
            Toast.makeText(getContext(), "No Category Selected!", Toast.LENGTH_SHORT).show();
            return 0; // Return null if no chip is selected
        } else
        {
            // Return the tag property of the selected chip
            Chip selectedCategory = chipGroupCategories.findViewById(selectedChipId);
            return (long) selectedCategory.getTag();
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

    // Get the repeat duration from the radio group
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