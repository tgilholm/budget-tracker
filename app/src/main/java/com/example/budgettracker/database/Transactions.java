package com.example.budgettracker.database;

// The transaction entity

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "transactions")
public class Transactions {

    // The unique Transaction ID acts as the primary key of the record
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "date")
    public Calendar date;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "repeat")
    public String repeat;
}
