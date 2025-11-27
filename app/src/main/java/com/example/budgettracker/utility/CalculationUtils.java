package com.example.budgettracker.utility;

// Holds stateless Calculation logic accessible throughout the application
// Used for basic calculations such as percentage
public class CalculationUtils
{
    private CalculationUtils() {}   // Declare constructor as private to prevent instantiation

    // Re-usable method for calculating percentage
    public static double calculatePercentage(double value, double total)
    {
        return (value / total) * 100;
    }

}
