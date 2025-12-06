package com.example.budgettracker.entities;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.budgettracker.enums.RepeatDuration;
import com.example.budgettracker.enums.TransactionType;


// The transaction class
// Stores the following details about a transaction
/*
    - The amount of money e.g. 50
    - The type of transaction i.e. incoming or outgoing
    - The date and time e.g. 10/1/2024 and 10:23
    - The category of transaction e.g. shopping
    - Repeating durations e.g. weekly

 */

// Also acts as an entity in the Room Database

@Entity(tableName = "transaction")
public final class Transaction {
    // Use a static field for the nextID parameter
    /*
    Each of the Transactions has access to the exact same copy of nextID, as it only exists once in memory
    due to it being a static field. It is incremented every time a new transaction is made.

    Since the transaction ID is never shown to the user, it does not matter that the nextID counter does not
    go down when the transaction is deleted.
     */
    private static long nextID;

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "amount")
    private final double amount;

    @ColumnInfo(name = "type")
    private final TransactionType type;

    @ColumnInfo(name = "datetime")
    private final Calendar dateTime;

    @ColumnInfo(name = "category")
    private final long categoryID;

    @ColumnInfo(name = "repeat")
    private final RepeatDuration repeatDuration;

    public Transaction(double amount, TransactionType type, Calendar dateTime, long categoryID, RepeatDuration repeatDuration) {
        // ID has a fixed width of 4
        this.id = nextID++; // Increment the ID counter by 1
        this.amount = amount;
        this.type = type;
        this.dateTime = dateTime;
        this.categoryID = categoryID;
        this.repeatDuration = repeatDuration;
    }

    // Getter methods
    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    // Returns date and time as a Calendar object
    public Calendar getDateTime() {
        return dateTime;
    }

    // Returns the date and time in the format hh:mm dd/MM/yyyy
    public String getDateTimeString() {
        return String.format(Locale.getDefault(), "%02d:%02d %02d/%02d/%d",
                dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE),
                dateTime.get(Calendar.DAY_OF_MONTH), dateTime.get(Calendar.MONTH) + 1, dateTime.get(Calendar.YEAR));
    }

    public long getCategoryID() {
        return categoryID;
    }

    public RepeatDuration getRepeatDuration() {
        return repeatDuration;
    }

    // toString method: Outputs an abridged version of a transaction
    @NonNull
    public String toString() {
        return String.format(Locale.getDefault(), "ID: %s, Amount: %.2f, Type: %s, Date: %s", id, amount, type, dateTime.toString());
    }

    // Used to compare one Transaction with another
    // By default equals() checks if memory locations are the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check if the two objects are exactly the same in memory
        if (o == null || getClass() != o.getClass())
            return false; // Check if the other object is not a Transaction or is null

        Transaction x = (Transaction) o; // Cast o to transaction- only executed if the last check succeeded
        // Each transaction will have a different time stamp, so this is used for comparisons

        return Double.compare(x.dateTime.getTimeInMillis(), dateTime.getTimeInMillis()) == 0;
    }

    // Overrides the default method to create a hashcode
    @Override
    public int hashCode() {
        // Generates a unique hashcode for this object using all of its fields
        return Objects.hash(id, amount, type, dateTime, categoryID, repeatDuration);
    }

    public void setId(long  id) {
        this.id = id;
    }
}
