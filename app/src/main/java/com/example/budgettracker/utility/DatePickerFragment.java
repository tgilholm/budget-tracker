package com.example.budgettracker.utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Extend the DialogFragment class to contain a time picker element
// The DialogFragment will float in the foreground of the activity
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Get the current time and set this as the default date.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        // Open the Date Picker
        return new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        // Use setFragmentResult() to send the result of the datePicker back to addFragment
        // This is done using a bundle, which contains a key, "dateKey", and the date with month + 1
        Bundle bundle = new Bundle();
        bundle.putString("dateKey", dayOfMonth + "/" + (month + 1) + "/" + year);
        getParentFragmentManager().setFragmentResult("dateKey", bundle); // Set the fragment result

    }
}
