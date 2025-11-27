package com.example.budgettracker.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.example.budgettracker.entities.Transaction;

import java.util.List;
import java.util.Objects;

public class TransactionDiffCallback extends DiffUtil.Callback
{
    private final List<Transaction> oldTransactionList;
    private final List<Transaction> newTransactionList;

    public TransactionDiffCallback(List<Transaction> oldTransactionList, List<Transaction> newTransactionList)
    {
        this.oldTransactionList = oldTransactionList;
        this.newTransactionList = newTransactionList;
    }

    // Calculate size of old list
    @Override
    public int getOldListSize()
    {
        return oldTransactionList.size();
    }

    // Calculate size of new list
    @Override
    public int getNewListSize()
    {
        return newTransactionList.size();
    }

    // Compare two transactions by ID
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
    {
        return Objects.equals(oldTransactionList.get(oldItemPosition).getId(), newTransactionList.get(newItemPosition).getId());
    }

    // Compare two transactions
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        Transaction oldTransaction = oldTransactionList.get(oldItemPosition);
        Transaction newTransaction = newTransactionList.get(newItemPosition);
        return Objects.equals(oldTransaction.getDateTime(), newTransaction.getDateTime());
    }
}
