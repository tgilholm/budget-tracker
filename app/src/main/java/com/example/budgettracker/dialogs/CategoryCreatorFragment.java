package com.example.budgettracker.dialogs;


// A dialog fragment that appears when the user clicks the
// "Add category" chip.
// It allows users to select a category name and a colour from a preset list

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Grid;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.adapters.ColorPickerAdapter;
import com.example.budgettracker.utility.ColorGridDecoration;
import com.example.budgettracker.utility.ColorHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CategoryCreatorFragment extends DialogFragment
{

    private final Context context;
    private int colorChoice;


    public CategoryCreatorFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_creator, container, false);


        // Get the list of colours from the colors.xml file
        Integer[] colorIDs = new Integer[]{
                R.color.red,
                R.color.hotPink,
                R.color.purple,
                R.color.darkPurple,
                R.color.darkBlue,
                R.color.lightBlue,
                R.color.blue,
                R.color.cyan,
                R.color.teal,
                R.color.green,
                R.color.limeGreen,
                R.color.yellowGreen,
                R.color.yellow,
                R.color.lightOrange,
                R.color.orange,
                R.color.darkOrange
        };

        int columnCount = 4;    // 4 Columns
        int spacing = 16;

        // Connect to the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.colorGrid);

        // Create an anonymous class extending GridLayoutManager to disable recycler view scrolling
        // This also maintains the ability to click on items
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount) {
            final boolean isScrollEnabled = false;

            @Override
            public boolean canScrollVertically() {
                return isScrollEnabled && super.canScrollVertically();
            }
        });


        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(context, List.of(colorIDs), selectedColour ->
        {
            // When a colour is selected, set it to "ticked" and get the colour from it
            colorChoice = selectedColour;

        });

        // Set the spaces between colour items cleanly
        recyclerView.addItemDecoration(new ColorGridDecoration(columnCount, spacing, false));
        recyclerView.setAdapter(colorPickerAdapter);


        return view;
    }
}
