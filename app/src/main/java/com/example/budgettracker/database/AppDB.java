package com.example.budgettracker.database;

import androidx.room.RoomDatabase;

public abstract class AppDB extends RoomDatabase{
    public abstract TransactionDAO transactionDAO();
}
