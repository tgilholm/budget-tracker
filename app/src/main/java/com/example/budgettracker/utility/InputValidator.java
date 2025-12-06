package com.example.budgettracker.utility;

/*
 Input validation methods (such as those used in getAmount()) are delegated here
 This helps to ensure the single-responsibility principle is adhered to
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.budgettracker.entities.Transaction;
import com.example.budgettracker.entities.TransactionWithCategory;
import com.example.budgettracker.enums.RepeatDuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class InputValidator
{
    // Todo change input validation methods to be more re-usable
    // todo length limits
    // Takes input to validate and context to pass to toast for error communication
    // Returns false for empty strings or non-matching strings and true otherwise
    public static boolean validateCurrencyInput(Context context, String input)
    {
        // Verify format is correct, inform user via toasts about any errors
        // Check if amountText is empty before proceeding
        if (input.isEmpty())
        {
            Toast.makeText(context, "Please enter a currency value", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Regular expression for checking amount format is correct
        /* Regex:
            Breakdown:
            [0-9]+          Matches if the string contains one-or-more numbers- i.e. 50
            [.]             Matches if the string contains a decimal point
            [0-9]{2}        Matches if numeric and ONLY 2 d.p. i.e. 50.12 instead of 50.123
            ([.][0-9]{2})?  The '?' indicates that the entire block is optional (zero or one)
                            This means that 50 is valid, and so is 50.12

            Complete regex: [0-9]+([.][0-9]{2})?
        */
        String regex = "[0-9]+([.][0-9]{2})?";

        // Use the regex to check whether the string meets the format "123" or "123.12"
        if (!(input.matches(regex)))
        {
            Toast.makeText(context, "Please enter a currency value in the format '123' or '123.45'", Toast.LENGTH_LONG).show();
            return false;
        }

        // Put the return in a try-catch in case the user tries any funny business not caught by the regex
        try
        {
            return true; // Return true if correctly formatted
        } catch (NumberFormatException funnyBusiness)
        {
            Toast.makeText(context, "Error getting currency value", Toast.LENGTH_LONG).show();
            Log.e("InputValidator Error: ", funnyBusiness.toString());
            return false;
        }
    }


    // todo rename and cleanup
    // Takes two String parameters, dateInput and timeInput and attempts to parse them into a Calendar object
    public static Calendar validateDateTimeInput(Context context, String dateTimeInput)
    {
        // Create a new Calendar instance
        Calendar c = Calendar.getInstance();

        // The format used by the program for dates is dd/mm/yyyy and time is hh/mm
        // Create a SimpleDateFormat to parse the date and time for instantiation of a Calendar object
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Attempt to create a new Calendar object and inject these values
        try
        {
            // Attempts to parse the date and time into a Date object
            Date parsedDateTime = timeFormat.parse(dateTimeInput);

            // If parsedDateTime is not null, update the calendar object
            if (parsedDateTime != null)
            {
                c.setTime(parsedDateTime);
            }
            return c;
        } catch (ParseException e)  // Inform the user via a toast if there are parsing errors
        {
            Toast.makeText(context, "Error parsing date/time", Toast.LENGTH_LONG).show();
            Log.e("AddFragment", "Error parsing date & time: " + e.getMessage());
            return null;
        }
    }

    // todo move
    public static RepeatDuration selectRepeatDuration(String input)
    {
        switch (input)
        {
            case "Never":
                return RepeatDuration.NEVER;
            case "Day":
                return RepeatDuration.DAILY;
            case "Week":
                return RepeatDuration.WEEKLY;
            case "Month":
                return RepeatDuration.MONTHLY;
            case "Year":
                return RepeatDuration.YEARLY;
            default:
                return null;
        }
    }

    // todo move to transaction calculator
    public static List<TransactionWithCategory> sortTransactions(List<TransactionWithCategory> transactions)
    {
        transactions.sort((o1, o2) ->
                o2.transaction.getDateTime().compareTo(o1.transaction.getDateTime()));
        return transactions;
    }
}