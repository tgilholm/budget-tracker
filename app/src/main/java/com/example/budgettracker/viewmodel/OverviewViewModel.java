package com.example.budgettracker.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgettracker.R;
import com.example.budgettracker.entities.Category;
import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.repositories.DataRepository;
import com.example.budgettracker.utility.ColorHandler;
import com.example.budgettracker.utility.StringUtils;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Abstracts the DataRepository */
public class OverviewViewModel extends AndroidViewModel
{
    private final Application application;
    private final DataRepository dataRepository;

    // On startup, get the current state of the database
    public OverviewViewModel(Application application)
    {
        super(application);

        this.application = application;
        dataRepository = DataRepository.getInstance(application);
    }


    // Exposes the LiveData version of the transaction list
    public LiveData<List<TransactionWithCategory>> getTransactions()
    {
        return dataRepository.getAllTransactions();
    }

    /* Business Logic */


    // Aggregates transactions into a map of Amount and Category
    // Map does not allow duplicate keys so it is the ideal choice
    @NonNull
    public static Map<Category, Double> getCategoryTotals(List<TransactionWithCategory> transactions)
    {
        Map<Category, Double> totalPerCategory = new HashMap<>();

        // Checks if transactions is null
        if (transactions == null)
        {
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
                totalPerCategory.merge(category, amount, Double::sum);
            }
        }
        return totalPerCategory;
    }

    // Aggregates the transactions into the top 3 categories with all others in "other"
    // Returns a PieDataSet with category colours attached
    public PieDataSet getPieData(List<TransactionWithCategory> transactions)
    {

        // DataSet
        PieDataSet dataSet = new PieDataSet(new ArrayList<>(), "");

        List<Integer> colorList = new ArrayList<>();

        // Get total spending
        double totalSpend = getTotalSpend(transactions);

        // Gets the total per category as a map
        Map<Category, Double> categoryTotals = getCategoryTotals(transactions);

        // Convert to a list for sorting
        List<Map.Entry<Category, Double>> entryList = new ArrayList<>(categoryTotals.entrySet());
        entryList.sort((lhs, rhs) -> rhs.getValue().compareTo(lhs.getValue())); // Sort the list by value (amount)

        // Hold the other total as the value of the other category
        Map<String, Double> top3 = new LinkedHashMap<>();
        double otherTotal = 0;

        // Adds everything up to 3 to its own category
        for (int i = 0; i < entryList.size(); i++)
        {
            Map.Entry<Category, Double> entry = entryList.get(i);

            if (i < 3) // if less than the limit
            {
                // Add a new entry to the PieEntries
                String label = StringUtils.formatLabel(entry.getKey().getName(), entry.getValue() / totalSpend * 100);

                // Add a colour to the dataSet
                colorList.add(ColorHandler.getColorARGB(application.getBaseContext(), entry.getKey().getColorID()));

                // Add the entry to the dataset
                dataSet.addEntry(new PieEntry(entry.getValue().floatValue(), label));
            } else
            {
                otherTotal += entry.getValue();
            }
        }

        // Add everything else to "other"
        if (otherTotal > 0)
        {
            String label = StringUtils.formatLabel("Other",  otherTotal / totalSpend * 100);
            dataSet.addEntry(new PieEntry((float) otherTotal, label));

            colorList.add(ColorHandler.getColorARGB(application.getBaseContext(), R.color.budgetBlue));
        }

        // Add the colourList to the dataset
        dataSet.setColors(colorList);

        // Return the dataSet
        return dataSet;

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
        if (transactions != null)
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
