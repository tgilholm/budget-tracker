package com.example.budgettracker.utility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.example.budgettracker.R;

// Handles methods related to colour
public final class ColorHandler
{


    // Converts a colorID to a ColorStateList
    @NonNull
    public static ColorStateList resolveColorID(int colorARGB)
    {
        return ColorStateList.valueOf(colorARGB);
    }

    public static int getColorARGB(Context context, int colorID)
    {
        return ContextCompat.getColor(context, colorID);
    }

    // Method to resolve XML-defined colours into those usable programmatically
    // Annotated ColorInt to specify a colour hex
    @ColorInt
    public static int getThemeColor(@NonNull Context context, @AttrRes int attributeColour) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeColour, typedValue, true);
        return typedValue.data;
    }

    // Set the text colour of a textview to red if the amount is negative and green otherwise
    public static void setAmountColour(@NonNull TextView textView, double amount)
    {
        Context context = textView.getContext();
        int color = (amount < 0) ? R.color.brightRed : R.color.brightGreen;
        textView.setTextColor(resolveColorID(getColorARGB(context, color)));
    }


    // Returns a colour of light or dark based on the luminance of the background colour
    @ColorInt
    public static int resolveForegroundColor(@NonNull Context context, @ColorInt int backgroundColour)
    {
        // Calculate the luminance of the background colour
        double luminance = ColorUtils.calculateLuminance(backgroundColour);

        Log.v("ColorHandler", "Luminance: " + luminance);

        // If luminance is greater than 0.5 luminance, return dark text
        if (luminance > 0.5)
        {
            return getColorARGB(context, R.color.text_fixed_dark);
        }
        else {
            return Color.WHITE;
        }

    }
}
