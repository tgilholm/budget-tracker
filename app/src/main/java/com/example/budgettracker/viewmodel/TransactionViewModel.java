package com.example.budgettracker.viewmodel;


import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.database.AppDB;
import com.example.budgettracker.database.TransactionDAO;
import com.example.budgettracker.enums.TransactionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 The TransactionViewModel for the application. This uses the Lifecycle-safe ViewModel to hold the
 data shared across fragments, such as the TransactionList. It uses the LiveData list to automatically
 trigger observers waiting for changes to the list.
 */

/*
All the database logic is handed to the TransactionRepository
Business logic is delegated to the TransactionCalculator
 */
public class TransactionViewModel extends AndroidViewModel
{
    /* Database logic */

    /*
    Hold the transactions in a MutableLiveData list. This allows the list to be exposed publicly
    To the application, but not to be modified unless through the addTransaction or removeTransaction methods
    */
    private final MutableLiveData<List<Transaction>> transactions;
    private final TransactionDAO transactionDAO;    // This is the only instance of transactionDAO
    private final ExecutorService executorService;    // ExecutorService for thread management

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
        executorService.execute(() ->
        {

            // Adds a transaction to the database
            transactionDAO.insertTransaction(newTransaction);
            loadFromDB(); // Updates the list from the database
        });
    }

    // Publicly accessible method to remove a transaction from the DB
    public void deleteTransaction(Transaction transaction)
    {
        executorService.execute(() ->
        {
            transactionDAO.delete(transaction);
            loadFromDB();
        });
    }

    // Load the transaction list with the one stored in the DB
    private void loadFromDB()
    {
        // Execute in a background thread
        executorService.execute(() ->
        {
            List<Transaction> transactionList = transactionDAO.getAll();

            // Post the result to the main thread
            transactions.postValue(transactionList);
            Log.v("TransactionViewModel", "loadFromDB called, size: " + transactionList.size());

        });
    }

    // Handle cleanup when the application is closed
    @Override
    protected void onCleared()
    {
        super.onCleared();
        executorService.shutdown();
    }


    /* Business Logic */


    // Groups categories into top-n and group the rest into "other"
    public Map<String, Double> getTopNCategoryTotals(List<Transaction> transactions, int n)
    {
        Map<String, Double> totalPerCategory = getCategoryTotals(transactions); // Get the total per category

        List<Map.Entry<String, Double>> entryList = new ArrayList<>(totalPerCategory.entrySet());                      // Convert to a list of Map entries for easy sorting
        entryList.sort((lhs, rhs) -> rhs.getValue().compareTo(lhs.getValue())); // Sort the list by value (amount)

        Map<String, Double> topN = new LinkedHashMap<>();
        double otherTotal = 0;

        // Adds everything up to n to its own category
        for (int i = 0; i < entryList.size(); i++)
        {
            if (i < n) // if less than the limit
            {
                topN.put(entryList.get(i).getKey(), entryList.get(i).getValue());
            }
            else
            {
                otherTotal += entryList.get(i).getValue();
            }
        }

        // Add everything else to "other"
        if (otherTotal > 0) {
            topN.put("Other", otherTotal);
        }
        return topN;
    }


    // Aggregates transactions into a map of Amount and Category
    // Map does not allow duplicate keys so it is the ideal choice
    @NonNull
    public static Map<String, Double> getCategoryTotals(List<Transaction> transactions)
    {
        Map<String, Double> totalPerCategory = new HashMap<>();

        // Checks if transactions is null
        if (transactions == null) {
            return totalPerCategory;    // Returns empty HashMap if so
        }

        // Put the transactions into the Map
        for (Transaction t : transactions)
        {
            String category = t.getCategory();
            double amount = t.getAmount();

            // Only handles "outgoing" transactions
            if (t.getType() == TransactionType.OUTGOING)
            {
                // Uses Map.merge() instead of if-else statements
                // Adds the amount to the total if the category already exists in the map
                // Otherwise, adds the category to the map with amount as the value
                totalPerCategory.merge(category, amount, Double::sum);
            }
        }
        return totalPerCategory;
    }

    // Calculate the remaining budget given a starting amount
    public double getBudgetRemaining(double start, List<Transaction> transactions)
    {
        if (transactions == null)
        {
            return start;
        }

        double result = start;
        for (Transaction t : transactions)
        {
            if (t.getType() == TransactionType.OUTGOING)
            {
                result -= t.getAmount();            // Subtract outgoings
            } else
            {
                result += t.getAmount();            // Add incoming
            }
        }
        return result;
    }

    // Calculates the amount spent across all outgoing transactions
    public double getTotalSpend(List<Transaction> transactions)
    {
        double total = 0;
        if (transactions != null )
        {
            for (Transaction t : transactions)
            {
                if (t.getType() == TransactionType.OUTGOING)
                {
                    total += t.getAmount();
                }
            }
        }
        return total;
    }
}
