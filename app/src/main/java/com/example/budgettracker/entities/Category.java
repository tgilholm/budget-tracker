package com.example.budgettracker.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public final class Category {
    private static long nextID;

    @PrimaryKey
    private long categoryID;                  // The unique ID of the category

    @ColumnInfo(name = "name")
    private final String name;          // The name of the category

    @ColumnInfo(name = "colour")
    private final int colorID;          // The id of the category (found in colors.xml)


    public Category(String name, int colorID) {
        this.categoryID = nextID++;
        this.name = name;
        this.colorID = colorID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long id)
    {
        this.categoryID = id;
    }

    public String getName() {
        return name;
    }

    public int getColorID() {
        return colorID;
    }
}
