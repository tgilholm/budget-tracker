package com.example.budgettracker.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgettracker.R;
import com.example.budgettracker.utility.InputValidator;
import com.example.budgettracker.viewmodel.BudgetViewModel;
import com.example.budgettracker.viewmodel.StartupViewModel;

// The FirstTimeStartup activity handles the case where the
// "notFirstRun" flag is false in the Preferences
public class FirstTimeStartupActivity extends AppCompatActivity {

    EditText budgetText;

    BudgetViewModel budgetViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get the layout from the XML file
        setContentView(R.layout.activity_first_startup);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        EdgeToEdge.enable(this);

        // Get the EditText from the view
        budgetText = findViewById(R.id.editTextBudget);

        // Get an instance of the BudgetViewModel
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
    }

    // Handle the button press with input validation
    public void startButtonPressed(View v) {
        double validatedInput = 0;
        String inputText = budgetText.getText().toString();

        if (!inputText.isEmpty()) {
            // Validate the input
            if (InputValidator.validateCurrencyInput(this, inputText))
            {
                validatedInput = Double.parseDouble(inputText);
                Log.v("FirstTimeStartupActivity", "Budget set: " + inputText);
            }
            else
            {
                Log.v("FirstTimeStartupActivity", "Invalid input: " + inputText);
            }
        }

        updatePreferences(validatedInput); // Call updatePreferences before returning
        returnToMain();
    }

    // Start an intent to return to the MainActivity
    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Set the firstRun flag to false and add the "budget" flag with the user amount
    private void updatePreferences(double budget)
    {
        // Get the shared preferences
        SharedPreferences prefs = this.getSharedPreferences(
                "appPreferences",
                Context.MODE_PRIVATE);

        // Create an editor for the shared preferences
        SharedPreferences.Editor editor = prefs.edit();

        // Set the notFirstRun flag to false
        editor.putBoolean("notFirstRun", true);
        editor.apply();

        // Update the budget through the BudgetViewModel
        budgetViewModel.setBudget(budget);

        Log.v("FirstTimeStartupActivity", "Updated appPreferences");
    }
}
