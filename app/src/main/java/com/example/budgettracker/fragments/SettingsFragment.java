package com.example.budgettracker.fragments;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.budgettracker.R;
import com.example.budgettracker.utility.InputValidator;

// The SettingsFragment contains the following options
/*
    Edit the pre-set budget
    Remove all data and start again (following a confirmation screen)
 */
public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey)
    {
        getPreferenceManager().setSharedPreferencesName("appPreferences"); // Use the appPreferences file
        addPreferencesFromResource(R.xml.preferences);


        // INPUT VALIDATION
        // Validate the change to a budget
        // This is done by intercepting the change and passing it through the InputValidator
        // before it can be saved to appPreferences

        EditTextPreference budgetPreference = findPreference("budget");
        if (budgetPreference != null) {

            // Use a PreferenceChangeListener to detect changes to the value
            // preference refers to the preference being changed, newValue is the value a user typed in
            budgetPreference.setOnPreferenceChangeListener((preference, newValue) ->
            {
                String validatedInput;
                try
                {
                    // Attempt to cast to a String
                    validatedInput = (String) newValue;

                    // Validate the input and show a toast if successful
                    if (InputValidator.validateCurrencyInput(getContext(), validatedInput)) {
                        Toast.makeText(getContext(), "Budget changed to: £" + validatedInput, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;

                } catch (Exception e)
                {
                    throw new RuntimeException("Invalid input");
                }
            });
        }

    }
}
