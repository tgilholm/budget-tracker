package com.example.budgettracker.fragments;

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
import com.example.budgettracker.utility.InputValidator;

import java.util.List;

/**
 * The fragment subclass for the Overview section of the app
 * Connects to fragment_overview.xml to provide layout
 */

public class OverviewFragment extends Fragment
{

    // Create an instance of the RecyclerViewAdapter
    private RecyclerViewAdapter recyclerViewAdapter;
    //private RecyclerViewAdapter

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

    /*TODO: Recent Transactions
    Get the most recent (last week or so) transactions from the file and display them in a
    list view, sorted by date.
     */


    public OverviewFragment()
    {
        // Required empty public constructor
    }



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

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
        transactionViewModel.getTransactions().observe(getViewLifecycleOwner(), transactionList ->
        {
            // When the transactionViewModel observes an update on transaction list, update the RecyclerView

            // Send the new (sorted) list to the recyclerViewAdapter
            recyclerViewAdapter.updateTransactions(InputValidator.sortTransactions(transactionList));

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


}