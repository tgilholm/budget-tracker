package com.example.budgettracker.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.budgettracker.enums.RepeatDuration;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.repositories.DataRepository;

import java.util.Calendar;

// The ViewModel that interacts with the AddFragment
public class AddViewModel extends AndroidViewModel
{
    // Get an instance of the DataRepository
    DataRepository dataRepository;

    public AddViewModel(Application application)
    {
        super(application);
        dataRepository = DataRepository.getInstance(application);
    }

    // Passes a new transaction to the Repository
    public void addTransaction(double amount, TransactionType type, Calendar dateTime, String category, RepeatDuration repeatDuration)
    {

    }
}
