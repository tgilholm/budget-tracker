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
import com.example.budgettracker.adapters.EditRecyclerViewAdapter;
import com.example.budgettracker.utility.InputValidator;

import java.util.List;

/**
 * The fragment subclass for the Transaction section of the app
 * Connects to fragment_transactions.xml to provide layout
 * Displays a RecyclerView with delete options for each transaction
 */

public class TransactionsFragment extends Fragment
{

    public TransactionsFragment()
    {
        super();
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Connect the transactionViewModel
        TransactionViewModel transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // Get the current transaction list
        List<Transaction> transactions = transactionViewModel.getTransactions().getValue();

        EditRecyclerViewAdapter editRecyclerViewAdapter;

        // Set up the editRecyclerViewAdapter
        if (transactions != null)
        {
            // Set the click listener for the delete button
            editRecyclerViewAdapter = new EditRecyclerViewAdapter(
                    InputValidator.sortTransactions(transactions), transactionViewModel::deleteTransaction,
                    R.layout.editable_transaction_item);
        } else
        {
            editRecyclerViewAdapter = null;
        }

        // Get the recycler view from the layout and set the adapter
        RecyclerView rvFullHistory = view.findViewById(R.id.rvFullHistory);
        rvFullHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFullHistory.setAdapter(editRecyclerViewAdapter);

        // Set an observer on the transaction list to update the Recycler View
        transactionViewModel.getTransactions().observe(getViewLifecycleOwner(), transactionList ->
        {
            // Add the list to the recyclerView on update
            if (editRecyclerViewAdapter != null)
            {
                editRecyclerViewAdapter.updateTransactions(InputValidator.sortTransactions(transactionList));

                // Scroll back to the top of the RecyclerView to show the new transaction
                if (rvFullHistory.getLayoutManager() != null)
                {
                    rvFullHistory.getLayoutManager().scrollToPosition(0);
                }
            }
        });
    }
}