package com.example.budgettracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;
import com.example.budgettracker.repositories.DataRepository;

import java.util.List;

// ViewModel corresponding to TransactionFragment
// Exposes list of transactions and supports deletion method
public class TransactionViewModel extends AndroidViewModel {

    private final DataRepository dataRepository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        dataRepository = DataRepository.getInstance(application);
    }

    // Exposes the LiveData version of the transaction list to the fragments
    public LiveData<List<TransactionWithCategory>> getTransactions()
    {
        return dataRepository.getAllTransactions();
    }

    // Delete a transaction
    public void deleteTransaction(TransactionWithCategory transactionWithCategory)
    {
        dataRepository.deleteTransaction(transactionWithCategory.transaction);
    }
}
