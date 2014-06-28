package net.myconbook.android.ui.elements;

import android.annotation.SuppressLint;
import android.database.Cursor;

import net.myconbook.android.Log;
import net.myconbook.android.content.DayList;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class ScheduleDayListItem implements Serializable {
    public static ScheduleDayListItem createFromCursor(Cursor c) {
        String day = c.getString(c.getColumnIndexOrThrow(DayList.DAY));
        return new ScheduleDayListItem(day);
    }

    public ScheduleDayListItem(String day) {
        _day = day;
    }

    private String _day;

    public String getDay() {
        return _day;
    }

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return format.parse(_day);
        } catch (ParseException e) {
            Log.e("Could not parse start date", e);
            return null;
        }
    }

    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate());
        cal.set(Calendar.HOUR_OF_DAY, 4);

        return cal;
    }

    public String getNiceName() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd");
        return format.format(getDate());
    }

    public boolean isToday() {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DAY_OF_YEAR) == getCalendar().get(Calendar.DAY_OF_YEAR)) {
            return true;
        }

        return false;
    }
}
