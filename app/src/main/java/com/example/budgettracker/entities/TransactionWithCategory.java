package com.example.budgettracker.entities;


import androidx.room.Embedded;
import androidx.room.Relation;

// Room performs a SQL JOIN on the Transaction and Category entities
// This allows accessing the category name in a Transaction
public class TransactionWithCategory
{
    @Embedded
    public Transaction transaction;

    @Relation(
            parentColumn = "category",
            entityColumn = "categoryID"
    )
    public Category category;
}
