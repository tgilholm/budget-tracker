package com.example.budgettracker.timeselector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Extend the DialogFragment class to contain a time picker element
// The DialogFragment will float in the foreground of the activity
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final Context context;

    public DatePickerFragment(Context context)
    {
        this.context = context;
    }

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
        return new DatePickerDialog(context, this, year, month, dayOfMonth);
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
