package net.myconbook.android.ui.elements;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.content.Hotels;

public class HotelListItem extends StandardListItem<HotelListItem.Holder> {
    private String _name;
    private String _address;
    private String _phone;
    private String _placeId;

    public HotelListItem(String name, String address, String phone, String placeId) {
        _name = name;
        _address = address;
        _phone = phone;
        _placeId = placeId;
    }

    public static HotelListItem createFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(Hotels.NAME));
        String address = c.getString(c.getColumnIndexOrThrow(Hotels.ADDRESS)).replace("\\n", "\n");
        String phone = c.getString(c.getColumnIndexOrThrow(Hotels.PHONE));
        String placeId = c.getString(c.getColumnIndexOrThrow(Hotels.PLACEID));

        return new HotelListItem(name, address, phone, placeId);
    }

    public String getName() {
        return _name;
    }

    public String getAddress() {
        return _address;
    }

    public String getPhone() {
        return _phone;
    }

    public String getPlaceId() {
        return _placeId;
    }

    public void populateViewHolder(Holder holder) {
        holder.name.setText(getName());
        holder.details.setText(getAddress() + "\n" + getPhone() + "\n");
    }

    public static class Holder extends StandardListItem.Holder {
        TextView name;
        TextView details;

        public Holder(View view) {
            name = (TextView) view.findViewById(android.R.id.text1);
            details = (TextView) view.findViewById(android.R.id.text2);
        }
    }
}
