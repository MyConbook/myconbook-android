package net.myconbook.android.ui.elements;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.content.Dealers;

public class DealerListItem extends SectionedListItem<DealerListItem.Holder> {
    private String _name;
    private String _location;
    private String _url;
    private String _desc;

    public DealerListItem(String name, String location, String url, String desc) {
        _name = name;
        _location = location;
        _url = url;
        _desc = desc;
    }

    public static DealerListItem createFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(Dealers.NAME));
        String table = c.getString(c.getColumnIndexOrThrow(Dealers.LOCATION));
        String url = c.getString(c.getColumnIndexOrThrow(Dealers.URL));
        String desc = c.getString(c.getColumnIndexOrThrow(Dealers.DESCRIPTION));

        return new DealerListItem(name, table, url, desc);
    }

    public String getName() {
        return _name;
    }

    public String getLocation() {
        return _location;
    }

    public String getURL() {
        return _url;
    }

    public String getDescription() {
        return _desc;
    }

    public void populateViewHolder(Holder holder) {
        holder.name.setText(getName());
        holder.location.setText(getLocation());
    }

    public static class Holder extends SectionedListItem.Holder {
        TextView name;
        TextView location;

        public Holder(View view) {
            super(view);
            name = (TextView) view.findViewById(android.R.id.text1);
            location = (TextView) view.findViewById(android.R.id.text2);
        }
    }
}
