package com.example.budgettracker.repositories;

import android.app.Application;

import com.example.budgettracker.database.AppDB;
import com.example.budgettracker.database.TransactionDAO;

import java.util.concurrent.ExecutorService;

// Provides a layer of abstraction over the Transaction and Category DAOs.

// Transaction, Overview and Add fragments interact with the DataRepository via UseCases
// The repository interacts with both the Transaction and Category tables (not User)
// Follows a singleton pattern to only permit ONE instance of the repository
public class DataRepository
{
    private final TransactionDAO transactionDAO;  // Holds an instance of the transactionDAO
    private final CategoryDAO categoryDAO;  // Holds an instance of the categoryDAO
    private final ExecutorService executorService; // Uses executorService to delegate DB operations to a thread pool

    // Constructor
    private DataRepository(Application application)
    {
        AppDB appDB = AppDB.getDBInstance(application); // Get an instance of the database

        // Initialise the DAO and executorService
        transactionDAO = appDB.transactionDAO();
        executorService = appDB.getExecutorService();

    }

}

