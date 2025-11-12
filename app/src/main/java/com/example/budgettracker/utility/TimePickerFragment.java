package com.example.budgettracker.utility;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Extend the DialogFragment class to contain a time picker element
// The DialogFragment will float in the foreground of the activity
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Use setFragmentResult() to send the result of the timePicker back to addFragment
        // This is done using a bundle, which contains a key, "timeKey", and the date with month + 1
        Bundle bundle = new Bundle();
        bundle.putString("timeKey", hourOfDay + ":" + minute);
        getParentFragmentManager().setFragmentResult("timeKey", bundle); // Set the fragment result

    }
}
