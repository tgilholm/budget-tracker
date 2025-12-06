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
import com.example.budgettracker.entities.TransactionWithCategory;
import com.example.budgettracker.utility.CalculationUtils;
import com.example.budgettracker.utility.ColorHandler;
import com.example.budgettracker.utility.Converters;
import com.example.budgettracker.utility.StringUtils;
import com.example.budgettracker.viewmodel.BudgetViewModel;
import com.example.budgettracker.viewmodel.OverviewViewModel;
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
    private OverviewViewModel overviewViewModel;

    private PieChart pieChart;

    private TextView txtBudgetRemaining;
    private TextView txtTotalBudget;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get the Views from the layout
        pieChart = view.findViewById(R.id.pieChart);                                // Get the pie chart from the layout
        txtBudgetRemaining = view.findViewById(R.id.txtBudgetRemaining);            // Get the budget remaining text from the layout
        txtTotalBudget = view.findViewById(R.id.txtTotalBudget);                    // Get the total budget text from the layout
        RecyclerView rvPartialHistory = view.findViewById(R.id.rvPartialHistory);   // Get the recycler view from the layout
        FloatingActionButton addButton = view.findViewById(R.id.overviewAddButton); // Get the FloatingActionButton from the layout

        setupPieChart();    // Set the PieChart styling options


        // Set up the Transaction and Budget ViewModels
        BudgetViewModel budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        overviewViewModel = new ViewModelProvider(requireActivity()).get(OverviewViewModel.class);


        // Instantiate the RecyclerView with an empty list (the observer will update it)
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<>(), R.layout.transaction_item);
        rvPartialHistory.setLayoutManager(new LinearLayoutManager(getContext()));       // Use a vertical LinearLayout as the layout manager
        rvPartialHistory.setAdapter(recyclerViewAdapter);                               // Connect to the recyclerViewAdapter


        // Set up an observer on the OverviewViewModel
        // Updates the RecyclerView, PieChart and BudgetRemaining when a new transaction is loaded from the DB
        overviewViewModel.getTransactions().observe(getViewLifecycleOwner(), transactionList ->
        {
            recyclerViewAdapter.updateTransactions(InputValidator.sortTransactions(transactionList));   // Update the RecyclerView
            updatePieChart(transactionList);    // Update the PieChart

            // Get the user's budget
            Double budget = budgetViewModel.getBudget().getValue();
            updateRemainingBudget(budget, transactionList);        // Invoke the method to update the remaining budget

            // Scroll back to the top of the RecyclerView to show the new transaction
            if (rvPartialHistory.getLayoutManager() != null)
            {
                rvPartialHistory.getLayoutManager().scrollToPosition(0);
            }
        });


        // Set up a listener on the Budget variable and invoke the method when changes are detected
        budgetViewModel.getBudget().observe(getViewLifecycleOwner(), budget -> {
            // Get the transaction list
            List<TransactionWithCategory> transactions = overviewViewModel.getTransactions().getValue();

            updateRemainingBudget(budget, transactions);
        });


        // Set up the FloatingActionButton to direct the user to the Add Fragment
        addButton.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putInt("addPage", 1);    // Send 1 to change the page title to 'Add'

            // Use FragmentResult to send a message to the MainActivity
            getParentFragmentManager().setFragmentResult("addPage", new Bundle());

        });
    }

    // Helper method to calculate the remaining budget
    // The budget needs to be recalculated if either the budget changes or a new transaction is added
    // This method is therefore able to be called in the observers for both values
    private void updateRemainingBudget(Double budget, List<TransactionWithCategory> transactions)
    {
        // If both values are non-null, recalculate the remaining budget
        if (budget != null && transactions != null)
        {
            double budgetRemaining = overviewViewModel.getBudgetRemaining(budget, transactions);

            // Display the remaining budget
            txtBudgetRemaining.setText(Converters.doubleToCurrencyString(budgetRemaining));

            // Display the total budget
            String outputString = "Monthly Budget: " + Converters.doubleToCurrencyString(budget);
            txtTotalBudget.setText(outputString);

            // Set the text colour to red if negative, green if positive
            ColorHandler.setAmountColour(txtBudgetRemaining, budgetRemaining);
        }
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



        // TODO Replace with RecyclerView
        // Set the legend of the pie chart
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);

        // Set the colour to the dynamic foreground colour
        legend.setTextColor(ColorHandler.getThemeColor(
                requireContext(),
                com.google.android.material.R.attr.colorOnSurfaceVariant
        ));
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

    private void updatePieChart(List<TransactionWithCategory> transactions)
    {
        if (transactions == null || transactions.isEmpty())
        {
            return;
        }

        // Get the top 3 categories and group the rest into other
        Map<String, Double> categoryTotals = overviewViewModel.getTopNCategoryTotals(transactions, 3);

        // Get the total spend
        double totalSpend = overviewViewModel.getTotalSpend(transactions);

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