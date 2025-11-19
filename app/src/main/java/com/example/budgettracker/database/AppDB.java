package com.example.budgettracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.budgettracker.Transaction;
import com.example.budgettracker.utility.Converters;

@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDB extends RoomDatabase
{
    public abstract TransactionDAO transactionDAO();

    // Define the instance of the Database as volatile to ensure all fragments receive updates
    // The DB_INSTANCE is defined "static" so only one instance exists across the application
    private static volatile AppDB DB_INSTANCE;

    // Publicly accessible method for threads to access the DB instance
    public static AppDB getDBInstance(final Context context)
    {
        // DOUBLE-CHECKED-LOCKING
        // First checks if the DB instance is null. If so, lock the method and continue.
        // Otherwise, skips over it entirely and returns the DB instance

        // If locked, check again to ensure another thread hasn't created an instance
        // Finally call databaseBuilder and return the instance

        if (DB_INSTANCE == null)
        {
            synchronized (AppDB.class) {
                if (DB_INSTANCE == null) {
                    // Create an instance of the type converter
                    Converters converter = new Converters();

                    // Build a new database
                    DB_INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDB.class,
                            "budgetbuddy_DB"  // Name of the DB itself
                    ).addTypeConverter(converter).build();
                }
            }
        }
        return DB_INSTANCE;
    }
}
