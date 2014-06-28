package net.myconbook.android.ui.elements;

import android.database.Cursor;

import net.myconbook.android.content.Stores;

public class StoreListItem extends GuideBaseItem<GuideBaseItem.Holder> {
    public StoreListItem(String name, String category, String address, String phone, String comments, String url, String thursday, String friday, String saturday, String sunday, String placeId) {
        super(name, category, address, phone, comments, url, thursday, friday, saturday, sunday, placeId, null);
    }

    public static StoreListItem createFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(Stores.NAME));
        String category = c.getString(c.getColumnIndexOrThrow(Stores.CATEGORY));
        String address = c.getString(c.getColumnIndexOrThrow(Stores.ADDRESS));
        String phone = c.getString(c.getColumnIndexOrThrow(Stores.PHONE));
        String comments = c.getString(c.getColumnIndexOrThrow(Stores.COMMENTS));
        String url = c.getString(c.getColumnIndexOrThrow(Stores.URL));
        String thursday = c.getString(c.getColumnIndexOrThrow(Stores.THURSDAY));
        String friday = c.getString(c.getColumnIndexOrThrow(Stores.FRIDAY));
        String saturday = c.getString(c.getColumnIndexOrThrow(Stores.SATURDAY));
        String sunday = c.getString(c.getColumnIndexOrThrow(Stores.SUNDAY));
        String placeId = c.getString(c.getColumnIndexOrThrow(Stores.PLACEID));

        return new StoreListItem(name, category, address, phone, comments, url, thursday, friday, saturday, sunday, placeId);
    }
}
