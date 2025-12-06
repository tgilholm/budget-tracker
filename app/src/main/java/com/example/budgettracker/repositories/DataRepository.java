package com.example.budgettracker.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.budgettracker.database.AppDB;
import com.example.budgettracker.database.CategoryDAO;
import com.example.budgettracker.database.TransactionDAO;
import com.example.budgettracker.entities.Category;
import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Provides a layer of abstraction over the Transaction and Category DAOs.

// Transaction, Overview and Add fragments interact with the DataRepository via UseCases
// The repository interacts with both the Transaction and Category tables (not User)
// Follows a singleton pattern to only permit ONE instance of the repository
public class DataRepository {
    private final TransactionDAO transactionDAO;  // Holds an instance of the transactionDAO
    private final CategoryDAO categoryDAO;  // Holds an instance of the categoryDAO
    private final ExecutorService executorService; // Uses executorService to delegate DB operations to a thread pool
    private static volatile DataRepository INSTANCE;    // The only instance of DataRepository

    // Constructor
    private DataRepository(Application application) {
        AppDB appDB = AppDB.getDBInstance(application); // Get an instance of the database

        // Initialise the DAOs and executorService
        transactionDAO = appDB.transactionDAO();
        categoryDAO = appDB.categoryDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public static DataRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (DataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    // Return the joined Transaction and Category tables
    public LiveData<List<TransactionWithCategory>> getAllTransactions() {
        return transactionDAO.getTransactionCategory();
    }

    public void insertTransaction(Transaction transaction) {
        // Run in a separate thread
        executorService.execute(() -> transactionDAO.insertTransaction(transaction));
    }

    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> transactionDAO.delete(transaction));
    }


    // CategoryDAO interface methods
    public LiveData<List<Category>> getAllCategories() {
        return categoryDAO.getAll();
    }

    public void insertCategory(Category category) {
        executorService.execute(() -> categoryDAO.insertCategory(category));
    }

    public Category getCategoryByID(long categoryID)
    {
        return categoryDAO.getCategoryByID(categoryID);
    }

    public void deleteCategory(Category category)
    {
        executorService.execute(() -> categoryDAO.delete(category));
    }

}

