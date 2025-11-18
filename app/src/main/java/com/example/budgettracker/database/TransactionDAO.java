package com.example.budgettracker.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.budgettracker.Transaction;

import java.util.List;

// The Data Access Object for the Transaction entity
// Abstracts the SQL calls behind methods
@Dao
public interface TransactionDAO {
    // Define each of the transactions needed here

    // Return the entire table
    @Query("SELECT * FROM transactions")
    List<Transaction> getAll();

    // Returns only the transaction matching the provided transaction ID
    @Query("SELECT * FROM transactions WHERE id = (:id)")
    Transaction getTransactionByID(int id);

    // Ellipsis after the parameter allows adding any number of transactions
    @Insert
    void insertTransaction(Transaction... transaction);

    // Removes a transaction from the table
    @Delete
    void delete(Transaction transaction);

}

