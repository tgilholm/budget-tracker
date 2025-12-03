package com.example.budgettracker.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;

import com.example.budgettracker.R;
import com.example.budgettracker.entities.Category;
import com.example.budgettracker.repositories.DataRepository;

import java.util.Arrays;
import java.util.List;

// Used to add default categories to the Category table in the DB
public class StartupViewModel extends AndroidViewModel
{

    private final DataRepository dataRepository;

    public StartupViewModel(Application application)
    {
        super(application);

        // Get an instance of the DataRepository
        dataRepository = DataRepository.getInstance(application);
    }

    // Query the database to check if there are any categories- if not, add them
    public void addDefaultCategories() {

        // Use observeForever to get the list of categories
        dataRepository.getAllCategories().observeForever(new Observer<>() {
            @Override
            public void onChanged(List<Category> categories) {
                // If the list of categories is null or empty, add the defaults
                if (categories == null || categories.isEmpty()) {
                    Log.v("StartupViewModel", "Adding defaults");
                    List<String> categoryNames = Arrays.asList("Entertainment", "Petrol", "Pets", "Travel", "Shopping");
                    List<Integer> colorIDs = Arrays.asList(R.color.lightGreen, R.color.lightBlue, R.color.lightRed, R.color.lightYellow, R.color.lightPurple);

                    for (String i : categoryNames) {
                        // Resolve the color IDs from colors.xml to a color integer before passing to Chip
                        int colorID = colorIDs.get(categoryNames.indexOf(i));

                        Category category = new Category(i, colorID);

                        // Add the default categories
                        dataRepository.insertCategory(category);
                    }
                }
                // Remove the observer
                dataRepository.getAllCategories().removeObserver(this);
            }
        });
    }
}
