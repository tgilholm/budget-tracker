package com.example.budgettracker.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;

import java.util.List;

// The Data Access Object for the Transaction entity
// Abstracts the SQL calls behind methods
@Dao
public interface TransactionDAO {
    // Define each of the transactions needed here

    // Return the entire table
    @Query("SELECT * FROM 'transaction'")
    LiveData<List<Transaction>> getAll();

    // Return the table joined with the category table
    @androidx.room.Transaction
    @Query("SELECT * FROM 'transaction'")
    LiveData<List<TransactionWithCategory>> getTransactionCategory();

    // Returns only the transaction matching the provided transaction ID
    @Query("SELECT * FROM 'transaction' WHERE id = (:id)")
    Transaction getTransactionByID(String id);

    // Ellipsis after the parameter allows adding any number of transactions
    @Insert
    void insertTransaction(Transaction... transaction);

    // Removes a transaction from the table
    @Delete
    void delete(Transaction transaction);

}

