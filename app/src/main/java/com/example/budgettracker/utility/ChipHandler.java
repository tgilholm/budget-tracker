package com.example.budgettracker.utility;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;

import com.example.budgettracker.R;
import com.example.budgettracker.entities.Category;
import com.google.android.material.chip.Chip;

// Handles the instantiation of new Chips
public final class ChipHandler
{

    // Takes a category and generates a chip
    @NonNull
    public static Chip createChip(@NonNull Context context, @NonNull Category category)
    {
        Chip chip = new Chip(context, null, R.style.Widget_BudgetTracker_ChipStyle);

        // Set the name of the chip to the category name
        chip.setText(category.getName());
        chip.setCheckable(true);
        chip.setClickable(true);

        int backgroundColor = ColorHandler.getColorARGB(context, category.getColorID());

        // Set the background color of the chip top the category's colour
        chip.setChipBackgroundColor(ColorHandler.resolveColorID(backgroundColor));

        // Set the chip text colour to adapt to the chip background colour
        chip.setTextColor(ColorHandler.resolveForegroundColor(context, backgroundColor));

        // Set the "tag" parameter of the Chip to the category ID
        // This facilitates the category selection logic
        chip.setTag(category.getCategoryID());
        return chip;
    }

    // Creates a chip with an add icon and an onclick to create a new category
    public static Chip createAddCategoryChip(@NonNull Context context)
    {
        ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.ThemeOverlay_BudgetTracker_AddChip);
        Chip chip = new Chip(newContext);
        chip.setChipIconResource(R.drawable.add_icon);
        chip.setText("");
        chip.setClickable(true);

        chip.setChipStartPadding(20);
        chip.setChipEndPadding(20);

        return chip;
    }
}
