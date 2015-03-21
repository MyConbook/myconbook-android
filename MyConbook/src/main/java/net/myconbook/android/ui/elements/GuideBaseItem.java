package net.myconbook.android.ui.elements;

import android.view.View;
import android.widget.TextView;

import net.myconbook.android.R;

import java.util.Calendar;

public abstract class GuideBaseItem<H extends SectionedListItem.Holder> extends SectionedListItem<H> {
    private String _name;
    private String _category;
    private String _address;
    private String _phone;
    private String _comments;
    private String _url;
    private String _thursday;
    private String _friday;
    private String _saturday;
    private String _sunday;
    private String _placeId;
    private String _yelpId;

    public GuideBaseItem(String name, String category, String address, String phone, String comments, String url, String thursday, String friday, String saturday, String sunday, String placeId, String yelpId) {
        _name = name;
        _category = category;
        _address = address;
        _phone = phone;
        _comments = comments;
        _url = url;
        _thursday = thursday;
        _friday = friday;
        _saturday = saturday;
        _sunday = sunday;
        _placeId = placeId;
        _yelpId = yelpId;
    }

    public static String getTimeText(String time) {
        if ((time == null) || (time.equals(""))) {
            return getString(R.string.closed);
        } else if (time.equals("?")) {
            return getString(R.string.unknown);
        } else {
            return time;
        }
    }

    protected static void setTimeText(TextView field, String time) {
        field.setText(getString(R.string.todays_hours) + getTimeText(time));
    }

    public String getName() {
        return _name;
    }

    public String getCategory() {
        return _category;
    }

    public String getAddress() {
        return _address;
    }

    public String getPhone() {
        return _phone;
    }

    public String getComments() {
        return _comments;
    }

    public String getURL() {
        return _url;
    }

    public boolean hasHours() {
        return ((_thursday != null) && (_friday != null) && (_saturday != null) && (_sunday != null));
    }

    public String getThursdayHours() {
        return _thursday;
    }

    public String getFridayHours() {
        return _friday;
    }

    public String getSaturdayHours() {
        return _saturday;
    }

    public String getSundayHours() {
        return _sunday;
    }

    public String getPlaceId() {
        return _placeId;
    }

    public String getYelpId() {
        return _yelpId;
    }

    public void populateViewHolder(Holder holder) {
        populateViewHolderParent(holder);
    }

    protected void populateViewHolderParent(Holder holder) {
        holder.name.setText(getName());
        holder.address.setText(getAddress());
        holder.phone.setText(getPhone());

        setTimeText(holder.hours, getTodayTime());
    }

    public String getTodayTime() {
        Calendar cal = Calendar.getInstance();
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.THURSDAY:
                return getThursdayHours();
            case Calendar.FRIDAY:
                return getFridayHours();
            case Calendar.SATURDAY:
                return getSaturdayHours();
            case Calendar.SUNDAY:
                return getSundayHours();
            default:
                return getString(R.string.not_available);
        }
    }

    public static class Holder extends SectionedListItem.Holder {
        TextView name;
        TextView address;
        TextView phone;
        TextView hours;

        public Holder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.tvName);
            address = (TextView) view.findViewById(R.id.tvAddress);
            phone = (TextView) view.findViewById(R.id.tvPhone);
            hours = (TextView) view.findViewById(R.id.tvTime);
        }
    }
}
