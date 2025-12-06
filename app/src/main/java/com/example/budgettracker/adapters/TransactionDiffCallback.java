package com.example.budgettracker.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;

import java.util.List;
import java.util.Objects;


// Uses DiffUtils to optimise RecyclerView updates
public class TransactionDiffCallback extends DiffUtil.Callback
{
    private final List<TransactionWithCategory> oldTransactionList;
    private final List<TransactionWithCategory> newTransactionList;

    public TransactionDiffCallback(List<TransactionWithCategory> oldTransactionList, List<TransactionWithCategory> newTransactionList)
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
        return Objects.equals(oldTransactionList.get(oldItemPosition).transaction.getId(), newTransactionList.get(newItemPosition).transaction.getId());
    }

    // Compare two transactions
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        Transaction oldTransaction = oldTransactionList.get(oldItemPosition).transaction;
        Transaction newTransaction = newTransactionList.get(newItemPosition).transaction;
        return Objects.equals(oldTransaction.getDateTime(), newTransaction.getDateTime());
    }
}
