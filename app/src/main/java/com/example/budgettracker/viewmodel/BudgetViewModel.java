package com.example.budgettracker.viewmodel;

// Similar format to TransactionViewModel
// The purpose is to centralise all "budget" interactions here
// The ViewModel is responsible for interfacing with SharedPreferences

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class BudgetViewModel extends AndroidViewModel {

    private final MutableLiveData<Double> budget = new MutableLiveData<>();

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor prefsEditor;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        prefs = getApplication().getSharedPreferences(
                "appPreferences",
                Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();

       budget.setValue(getBudgetFromPrefs());
    }

    // Retrieve the "budget" field from the SharedPreferences
    private double getBudgetFromPrefs()
    {
        // preferences.xml always stores the budget as a string
        return Double.parseDouble(prefs.getString("budget", "0")); // default to 0
    }

    // Return the value of the budget
    public MutableLiveData<Double> getBudget() {
        return budget;
    }

    public void setBudget(double _budget) {
        // Take a double parameter and set the "budget" field in app preferences
        prefsEditor.putString("budget", "" + _budget);
        prefsEditor.apply();
        getBudgetFromPrefs();
    }

}
