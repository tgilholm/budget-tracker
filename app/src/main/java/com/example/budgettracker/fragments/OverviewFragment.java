package com.example.budgettracker.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgettracker.R;
import com.example.budgettracker.Transaction;
import com.example.budgettracker.TransactionViewModel;
import com.example.budgettracker.adapters.RecyclerViewAdapter;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.utility.InputValidator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The fragment subclass for the Overview section of the app
 * Connects to fragment_overview.xml to provide layout
 */

public class OverviewFragment extends Fragment
{

    // Create an instance of the RecyclerViewAdapter
    private RecyclerViewAdapter recyclerViewAdapter;

    private PieChart pieChart;

    /* TODO: Update remaining budget
    Update txtBudgetAmount when new transactions are added.
    Subtract the total outgoings from the total budget to get the remaining budget
     */

    /* TODO: Pie-chart functionality
    Pie charts will have colour-coded slices representing constituent categories
    They will automatically calculate how much of the spending is taken up by which category
    The user has the option to change the time span the average is taken from (default week)
    Categories are then shown to the right of the pie chart, ordered by percentage
     */


    public OverviewFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.pieChart);    // Get the pie chart from the layout
        setupPieChart();


        // Connect the TransactionViewModel to the same one in MainActivity
        // Create an instance of the TransactionViewModel
        TransactionViewModel transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // Get the current Transaction list and convert it to a standard List
        List<Transaction> transactions = transactionViewModel.getTransactions().getValue();

        // Set up the recyclerViewAdapter with the current (sorted) transaction list
        if (transactions != null)
        {
            recyclerViewAdapter = new RecyclerViewAdapter(InputValidator.sortTransactions(transactions), R.layout.transaction_item);
        }

        // Set up the recycler view
        RecyclerView rvPartialHistory = view.findViewById(R.id.rvPartialHistory);
        rvPartialHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPartialHistory.setAdapter(recyclerViewAdapter);

        // Set up an observer on the TransactionViewModel
        // Updates the RecyclerView, PieChart and BudgetRemaining with the new data
        transactionViewModel.getTransactions().observe(getViewLifecycleOwner(), transactionList ->
        {
            // When the transactionViewModel observes an update on transaction list, update the RecyclerView
            // Send the new (sorted) list to the recyclerViewAdapter
            recyclerViewAdapter.updateTransactions(InputValidator.sortTransactions(transactionList));

            // Update the PieChart
            updatePieChart(transactionList);

            // Scroll back to the top of the RecyclerView to show the new transaction
            if (rvPartialHistory.getLayoutManager() != null)
            {
                rvPartialHistory.getLayoutManager().scrollToPosition(0);
            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    // Set up the pie chart
    // Gets each of the categories and the total spending (currently for all time)
    // Gets the number of items in each category

    // Calculates the proportion of each element

    // TODO only add outgoing spending to the pie chart (not income)

    // Sets up the styling of the pie chart
    private void setupPieChart()
    {
        // Set pie chart properties
        pieChart.setUsePercentValues(true);                             // Calculate by category percentage
        pieChart.getDescription().setEnabled(false);                    // Remove the description label
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);                       // Sets a hole in the middle of the cart
        pieChart.setHoleRadius(40f);                                    // Make the hole smaller
        pieChart.setDrawEntryLabels(false);                             // Remove the labels from slices

        // Set the legend of the pie chart
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setTypeface(Typeface.MONOSPACE);                         // Use a monospace font so string padding works properly

        // Set the alignment to be to the centre right of the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);       // Stack the legend vertically

        // Set the draw location of the legend
        legend.setDrawInside(false);
        legend.setXEntrySpace(0f);     // X offset
        legend.setYEntrySpace(0f);      // Y offset

    }


    // Add new data to the pie chart
    // Displays only the top three categories by name, then aggregates the rest as "other"
    // Also displays a legend of categories and the percentage they take up
    // Pads the label with spaces between the category name and percentage for readability

    // todo diffutils?
    // todo select by category on transactions page
    private void updatePieChart(List<Transaction> transactions)
    {
        int padLength = 16;

        // Get the total spending per category
        Map<String, Double> totalPerCategory = getStringDoubleMap(transactions);

        // Calculate the total of all spending
        double totalSpend = 0;
        for (Double value : totalPerCategory.values())
        {
            totalSpend += value;
        }

        // Convert the entrySet to a list and sort by amount
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(totalPerCategory.entrySet());
        entryList.sort((lhs, rhs) -> rhs.getValue().compareTo(lhs.getValue()));


        // Add the top three categories as PieEntries to the chart
        List<PieEntry> pieEntries = new ArrayList<>();
        double otherTotal = 0; // Aggregate anything beyond i=4 into one category

        for (int i = 0; i < entryList.size(); i++)
        {
            if (i < 3)
            {
                String label = String.format(Locale.getDefault(), "%s %.2f%%",
                        padLeft(entryList.get(i).getKey() + ":", padLength),
                        calculatePercentage(entryList.get(i).getValue(), totalSpend));

                pieEntries.add(new PieEntry(entryList.get(i).getValue().floatValue(), label));
            }
            // Add anything else to the otherTotal
            else
            {
                otherTotal += entryList.get(i).getValue();
            }
        }

        // Add the "other" category
        if (otherTotal > 0)
        {
            String label = String.format(Locale.getDefault(), "%s %.2f%%",
                    padLeft("Other:", padLength),
                    calculatePercentage(otherTotal, totalSpend));

            pieEntries.add(new PieEntry((float) otherTotal, label));
        }

        // Add the pie entries to the a dataSet
        PieDataSet dataSet = new PieDataSet(pieEntries, "");

        // todo custom colors
        // Style the dataset
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Add some sample colours
        dataSet.setValueFormatter(new PercentFormatter(pieChart));
        dataSet.setSliceSpace(2f);
        dataSet.setDrawValues(false); // Remove the labels on the slices themselves

        pieChart.setData(new PieData(dataSet));
        pieChart.invalidate();


    }


    // Separate method to convert the list of transactions to a Map of String:Double values
    @NonNull
    private static Map<String, Double> getStringDoubleMap(List<Transaction> transactions)
    {
        // Aggregate transactions by category
        // Map does not allow duplicate keys so it is the ideal choice

        Map<String, Double> totalPerCategory = new HashMap<>();

        // Put the transactions into the Map
        for (Transaction t : transactions)
        {
            String category = t.getCategory();
            double amount = t.getAmount();

            // Only add "outgoing" transactions
            if (t.getType() == TransactionType.OUTGOING)
            {
                if (totalPerCategory.containsKey(category))
                {
                    // If the category already exists in the map, add the amount to the total
                    totalPerCategory.put(category, totalPerCategory.get(category) + amount);
                } else
                {
                    // If the category does not exist in the map, add it with the amount
                    totalPerCategory.put(category, amount);
                }
            }
        }
        return totalPerCategory;
    }

    // Re-usable method for calculating percentage
    private double calculatePercentage(double value, double total)
    {
        return (value / total) * 100;
    }

    // Used to pad the percentage text in the category labels so they line up properly
    private String padLeft(String inputString, int length)
    {
        if (inputString.length() >= length) {
            return inputString;
        }
        else {
            return String.format(Locale.getDefault(), "%-" + length + "s", inputString);
        }
    }
}