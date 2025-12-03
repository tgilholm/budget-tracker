package com.example.budgettracker.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Locale;
import java.util.Objects;

@Entity(tableName = "category")
public final class Category {
    private static long nextID;

    @NonNull
    @PrimaryKey
    private String id;                  // The unique ID of the category

    @ColumnInfo(name = "name")
    private final String name;          // The name of the category

    @ColumnInfo(name = "colour")
    private final int colorID;          // The id of the category (found in colors.xml)


    public Category(String name, int colorID) {
        this.id = String.format(Locale.getDefault(), "%04d", nextID++);
        this.name = name;
        this.colorID = colorID;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColorID() {
        return colorID;
    }
}
