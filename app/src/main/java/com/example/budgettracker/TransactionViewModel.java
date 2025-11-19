package com.example.budgettracker;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.budgettracker.database.AppDB;
import com.example.budgettracker.database.TransactionDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 The TransactionViewModel for the application. This uses the Lifecycle-safe ViewModel to hold the
 data shared across fragments, such as the TransactionList. It uses the LiveData list to automatically
 trigger observers waiting for changes to the list.
 */

/*
All the database logic is handled within the TransactionView model
 */
public class TransactionViewModel extends AndroidViewModel
{
    /*
    Hold the transactions in a MutableLiveData list. This allows the list to be exposed publicly
    To the application, but not to be modified unless through the addTransaction or removeTransaction methods
    */
    private final MutableLiveData<List<Transaction>> transactions;

    // This is the only instance of transactionDAO
    private final TransactionDAO transactionDAO;

    // ExecutorService for thread management
    private final ExecutorService executorService;

    // On startup, get the current state of the database
    public TransactionViewModel(Application application)
    {
        super(application);

        // ONLY ONE instance of the database
        AppDB appDB = AppDB.getDBInstance(application.getApplicationContext());

        executorService = Executors.newSingleThreadExecutor();
        transactionDAO = appDB.transactionDAO(); // Start the DAO
        transactions = new MutableLiveData<>(new ArrayList<>());
        loadFromDB();
    }


    // Exposes the LiveData version of the transaction list to the fragments
    public LiveData<List<Transaction>> getTransactions()
    {
        return transactions;    // Returns the immutable list
    }


    // Adds a transaction to the list. Publicly accessible to all fragments
    public void addTransaction(Transaction newTransaction)
    {
        // Execute in a background thread
        executorService.execute(() -> {

            // Adds a transaction to the database
            transactionDAO.insertTransaction(newTransaction);
            loadFromDB(); // Updates the list from the database
        });
    }

    // Load the transaction list with the one stored in the DB
    private void loadFromDB()
    {
        // Execute in a background thread
        executorService.execute(() -> {
            List<Transaction> transactionList = transactionDAO.getAll();

            // Post the result to the main thread
            transactions.postValue(transactionList);

        });
    }

    // Handle cleanup when the application is closed
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }



}
