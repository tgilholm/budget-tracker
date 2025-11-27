package com.example.budgettracker.utility;

import java.util.Locale;

// Stateless utility class for string operations such as padding and formatting.
public class StringUtils
{
    private StringUtils() {}

    // Returns a string padded with spaces to a specified length
    private static String padRight(String s, int l)
    {
        if (s.length() >= l)
        {
            return s;
        } else
        {
            return String.format(Locale.getDefault(), "%-" + l + "s", s);
        }
    }

    // Takes a string and a double and formats them like so
    // CategoryName        xx%
    public static String formatLabel(String s, double n)
    {
        return String.format(Locale.getDefault(), "%s %.1f%%",
                padRight(s, 16), n);
    }


}
