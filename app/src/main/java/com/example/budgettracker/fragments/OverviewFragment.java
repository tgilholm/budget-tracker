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
import android.widget.TextView;

import com.example.budgettracker.R;
import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.utility.CalculationUtils;
import com.example.budgettracker.utility.ColorHandler;
import com.example.budgettracker.utility.Converters;
import com.example.budgettracker.utility.StringUtils;
import com.example.budgettracker.viewmodel.BudgetViewModel;
import com.example.budgettracker.viewmodel.TransactionViewModel;
import com.example.budgettracker.adapters.RecyclerViewAdapter;
import com.example.budgettracker.utility.InputValidator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * The fragment subclass for the Overview section of the app
 * Connects to fragment_overview.xml to provide layout
 */

public class OverviewFragment extends Fragment
{
    private RecyclerViewAdapter recyclerViewAdapter;
    private TransactionViewModel transactionViewModel;

    private PieChart pieChart;

    private TextView txtBudgetRemaining;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get the Views from the layout
        pieChart = view.findViewById(R.id.pieChart);                                // Get the pie chart from the layout
        txtBudgetRemaining = view.findViewById(R.id.txtBudgetRemaining);            // Get the budget remaining text from the layout
        RecyclerView rvPartialHistory = view.findViewById(R.id.rvPartialHistory);   // Get the recycler view from the layout
        FloatingActionButton addButton = view.findViewById(R.id.overviewAddButton); // Get the FloatingActionButton from the layout

        setupPieChart();    // Set the PieChart styling options


        // Set up the Transaction and Budget ViewModels
        BudgetViewModel budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);


        // Instantiate the RecyclerView with an empty list (the observer will update it)
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<>(), R.layout.transaction_item);
        rvPartialHistory.setLayoutManager(new LinearLayoutManager(getContext()));       // Use a vertical LinearLayout as the layout manager
        rvPartialHistory.setAdapter(recyclerViewAdapter);                               // Connect to the recyclerViewAdapter


        // Helper method to calculate the remaining budget
        // The budget needs to be recalculated if either the budget changes or a new transaction is added
        // This method is therefore able to be called in the observers for both values
        Runnable updateRemainingBudget = () ->
        {

            Double initialBudget = budgetViewModel.getBudget().getValue();                      // Get the user's budget from the ViewModel
            List<Transaction> transactions = transactionViewModel.getTransactions().getValue(); // Get the list of transactions from the ViewModel

            // If both values are non-null, recalculate the remaining budget
            if (initialBudget != null && transactions != null)
            {
                double budgetRemaining = transactionViewModel.getBudgetRemaining(initialBudget, transactions);
                txtBudgetRemaining.setText(Converters.doubleToCurrencyString(budgetRemaining));

                // Set the text colour to red if negative, green if positive
                ColorHandler.setAmountColour(txtBudgetRemaining, budgetRemaining);
            }
        };

        // Set up an observer on the TransactionViewModel
        // Updates the RecyclerView, PieChart and BudgetRemaining when a new transaction is loaded from the DB
        transactionViewModel.getTransactions().observe(getViewLifecycleOwner(), transactionList ->
        {
            recyclerViewAdapter.updateTransactions(InputValidator.sortTransactions(transactionList));   // Update the RecyclerView
            updatePieChart(transactionList);    // Update the PieChart
            updateRemainingBudget.run();        // Invoke the Runnable to update the remaining budget

            // Scroll back to the top of the RecyclerView to show the new transaction
            if (rvPartialHistory.getLayoutManager() != null)
            {
                rvPartialHistory.getLayoutManager().scrollToPosition(0);
            }
        });


        // Set up a listener on the Budget variable and invoke the Runnable when changes are detected
        budgetViewModel.getBudget().observe(getViewLifecycleOwner(), budget -> updateRemainingBudget.run());


        // Set up the FloatingActionButton to direct the user to the Add Fragment
        addButton.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putInt("addPage", 1);    // Send 1 to change the page title to 'Add'

            // Use FragmentResult to send a message to the MainActivity
            getParentFragmentManager().setFragmentResult("addPage", new Bundle());

        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }


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
    // todo stop using the legend and use a recycler view instead

    private void updatePieChart(List<Transaction> transactions)
    {
        if (transactions == null || transactions.isEmpty())
        {
            return;
        }

        // Get the top 3 categories and group the rest into other
        Map<String, Double> categoryTotals = transactionViewModel.getTopNCategoryTotals(transactions, 3);

        // Get the total spend
        double totalSpend = transactionViewModel.getTotalSpend(transactions);

        // Create a list of pie entries
        List<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet())
        {
            String label = StringUtils.formatLabel(
                    entry.getKey(),
                    CalculationUtils.calculatePercentage(entry.getValue(), totalSpend));
            pieEntries.add(new PieEntry(entry.getValue().floatValue(), label));
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


}