package com.example.budgettracker.adapters;

import com.example.budgettracker.Transaction;

import java.util.List;

// Extends the RecyclerViewAdapter to support removing transactions
public class EditRecyclerViewAdapter extends RecyclerViewAdapter{
    public EditRecyclerViewAdapter(List<Transaction> transactions) {
        super(transactions);
    }
}
