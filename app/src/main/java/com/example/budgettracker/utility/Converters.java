package com.example.budgettracker.utility;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Locale;

// Used to convert types into those able to be stored by RoomDB
@ProvidedTypeConverter
public class Converters {

    @TypeConverter
    public long fromCalendar(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    @TypeConverter
    public Calendar fromLong(Long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return c;
    }

    public static String calendarToHourMinute(Calendar calendar) {
        return String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public static String calendarToDayMonthYear(Calendar calendar) {
        return String.format(Locale.getDefault(), "%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

    }

}
