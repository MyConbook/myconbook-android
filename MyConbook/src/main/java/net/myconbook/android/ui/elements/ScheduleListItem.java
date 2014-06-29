package net.myconbook.android.ui.elements;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.content.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class ScheduleListItem extends SectionedListItem<ScheduleListItem.Holder> {
    private SimpleDateFormat _headerDisplay;
    private String _name;
    private String _details;
    private Date _startTime;
    private Date _endTime;
    private String _category;
    private String _room;

    public ScheduleListItem(String name, String details, Date startTime, Date endTime, String category, String room) {
        _name = name;
        _details = details;
        _startTime = startTime;
        _endTime = endTime;
        _category = category;
        _room = room;

        _headerDisplay = new SimpleDateFormat("h:mma");
    }

    public static ScheduleListItem createFromCursor(Cursor c) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        String name = c.getString(c.getColumnIndexOrThrow(Schedule.TITLE));
        String desc = c.getString(c.getColumnIndexOrThrow(Schedule.DESC));
        String startDateStr = c.getString(c.getColumnIndexOrThrow(Schedule.START));

        Date startDate = null;
        try {
            startDate = iso8601Format.parse(startDateStr);
        } catch (ParseException e) {
            Log.e("Could not parse start date", e);
            Log.c(e);
        }

        String endDateStr = c.getString(c.getColumnIndexOrThrow(Schedule.END));

        Date endDate = null;
        try {
            endDate = iso8601Format.parse(endDateStr);
        } catch (ParseException e) {
            Log.e("Could not parse end date", e);
            Log.c(e);
        }

        String category = c.getString(c.getColumnIndexOrThrow(Schedule.CATEGORY));
        String room = c.getString(c.getColumnIndexOrThrow(Schedule.LOCATION)).replace("* ", "").replace("*", "").trim();

        return new ScheduleListItem(name, desc, startDate, endDate, category, room);
    }

    public String getName() {
        return _name;
    }

    public String getDetails() {
        return _details;
    }

    public Date getStartTime() {
        return _startTime;
    }

    public Date getEndTime() {
        return _endTime;
    }

    public String getTime() {
        return _headerDisplay.format(_startTime) + " - " + _headerDisplay.format(_endTime);
    }

    public String getCategory() {
        return _category;
    }

    public String getRoom() {
        return _room;
    }

    public void populateViewHolder(Holder holder) {
        holder.name.setText(getName());
        holder.location.setText(getRoom());
        holder.time.setText(getTime());
    }

    public static class Holder extends SectionedListItem.Holder {
        TextView name;
        TextView location;
        TextView time;

        public Holder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.tvName);
            location = (TextView) view.findViewById(R.id.tvAddress);
            time = (TextView) view.findViewById(R.id.tvTime);
        }
    }
}
