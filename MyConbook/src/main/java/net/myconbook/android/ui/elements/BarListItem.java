package net.myconbook.android.ui.elements;

import android.database.Cursor;

import net.myconbook.android.content.Bars;

public class BarListItem extends GuideBaseItem<GuideBaseItem.Holder> {
    public BarListItem(String name, String category, String address, String phone, String comments, String url, String thursday, String friday, String saturday, String sunday, String placeId, String yelpId) {
        super(name, category, address, phone, comments, url, thursday, friday, saturday, sunday, placeId, yelpId);
    }

    public static BarListItem createFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(Bars.NAME));
        String category = c.getString(c.getColumnIndexOrThrow(Bars.CATEGORY));
        String address = c.getString(c.getColumnIndexOrThrow(Bars.ADDRESS));
        String phone = c.getString(c.getColumnIndexOrThrow(Bars.PHONE));
        String comments = c.getString(c.getColumnIndexOrThrow(Bars.COMMENTS));
        String url = c.getString(c.getColumnIndexOrThrow(Bars.URL));
        String thursday = c.getString(c.getColumnIndexOrThrow(Bars.THURSDAY));
        String friday = c.getString(c.getColumnIndexOrThrow(Bars.FRIDAY));
        String saturday = c.getString(c.getColumnIndexOrThrow(Bars.SATURDAY));
        String sunday = c.getString(c.getColumnIndexOrThrow(Bars.SUNDAY));
        String placeId = c.getString(c.getColumnIndexOrThrow(Bars.PLACEID));
        String yelpId = c.getString(c.getColumnIndexOrThrow(Bars.YELPID));

        return new BarListItem(name, category, address, phone, comments, url, thursday, friday, saturday, sunday, placeId, yelpId);
    }
}
