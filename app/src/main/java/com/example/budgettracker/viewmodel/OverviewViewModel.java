package com.example.budgettracker.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgettracker.entities.Category;
import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.repositories.DataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Abstracts the DataRepository */
public class OverviewViewModel extends AndroidViewModel
{
    private final DataRepository dataRepository;

    // On startup, get the current state of the database
    public OverviewViewModel(Application application)
    {
        super(application);
        dataRepository = DataRepository.getInstance(application);
    }


    // Exposes the LiveData version of the transaction list to the fragments
    public LiveData<List<TransactionWithCategory>> getTransactions()
    {
        return dataRepository.getAllTransactions();
    }

    /* Business Logic */


    // Groups categories into top-n and group the rest into "other"
    public Map<String, Double> getTopNCategoryTotals(List<TransactionWithCategory> transactions, int n)
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
    public static Map<String, Double> getCategoryTotals(List<TransactionWithCategory> transactions)
    {
        Map<String, Double> totalPerCategory = new HashMap<>();

        // Checks if transactions is null
        if (transactions == null) {
            return totalPerCategory;    // Returns empty HashMap if so
        }

        // Put the transactions into the Map
        for (TransactionWithCategory t : transactions)
        {
            Transaction transaction = t.transaction;
            Category category = t.category;
            double amount = transaction.getAmount();

            // Only handles "outgoing" transactions
            if (transaction.getType() == TransactionType.OUTGOING)
            {
                // Uses Map.merge() instead of if-else statements
                // Adds the amount to the total if the category already exists in the map
                // Otherwise, adds the category to the map with amount as the value
                totalPerCategory.merge(category.getName(), amount, Double::sum);
            }
        }
        return totalPerCategory;
    }

    // Calculate the remaining budget given a starting amount
    public double getBudgetRemaining(double start, List<TransactionWithCategory> transactions)
    {
        if (transactions == null)
        {
            return start;
        }

        double result = start;
        for (TransactionWithCategory t : transactions)
        {
            Transaction transaction = t.transaction;
            if (transaction.getType() == TransactionType.OUTGOING)
            {
                result -= transaction.getAmount();            // Subtract outgoings
            } else
            {
                result += transaction.getAmount();            // Add incoming
            }
        }
        return result;
    }

    // Calculates the amount spent across all outgoing transactions
    public double getTotalSpend(List<TransactionWithCategory> transactions)
    {
        double total = 0;
        if (transactions != null )
        {
            for (TransactionWithCategory t : transactions)
            {
                Transaction transaction = t.transaction;
                if (transaction.getType() == TransactionType.OUTGOING)
                {
                    total += transaction.getAmount();
                }
            }
        }
        return total;
    }
}
