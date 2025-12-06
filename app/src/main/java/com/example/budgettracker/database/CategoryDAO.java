package com.example.budgettracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.budgettracker.entities.Category;

import java.util.List;


// The Data Access Object for the Category entity
@Dao
public interface CategoryDAO
{
    // Return the list of categories
    @Query("SELECT * FROM 'category'")
    LiveData<List<Category>> getAll();

    // Get a category by ID
    @Query("SELECT * FROM 'category' WHERE categoryID = (:id)")
    Category getCategoryByID(long id);

    // Add a new category
    @Insert
    void insertCategory(Category... categories);

    // TODO delete transaction logic
    // Delete a specific category
    @Delete
    void delete(Category category);


}
