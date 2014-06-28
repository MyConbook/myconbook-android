package net.myconbook.android.ui.elements;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.R;
import net.myconbook.android.content.ConInfo;

public class ConInfoListItem extends StandardListItem<ConInfoListItem.Holder> {
    private String _name;
    private String _location;
    private String _details;
    private String _mapName;

    public ConInfoListItem(String name, String location, String details, String mapName) {
        _name = name;
        _location = location;
        _details = details;
        _mapName = mapName;
    }

    public static ConInfoListItem createFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(ConInfo.NAME));
        String location = c.getString(c.getColumnIndexOrThrow(ConInfo.LOCATION));
        String details = c.getString(c.getColumnIndexOrThrow(ConInfo.DETAILS)).replace("\\n", "\n");
        String mapName = c.getString(c.getColumnIndexOrThrow(ConInfo.MAP_NAME));

        return new ConInfoListItem(name, location, details, mapName);
    }

    public String getName() {
        return _name;
    }

    public String getLocation() {
        return _location;
    }

    public String getDetails() {
        return _details;
    }

    public String getMapName() {
        return _mapName;
    }

    public void populateViewHolder(Holder holder) {
        holder.name.setText(getName());
        holder.location.setText(getLocation());
        holder.details.setText(getDetails());
    }

    public static class Holder extends StandardListItem.Holder {
        TextView name;
        TextView location;
        TextView details;

        public Holder(View view) {
            name = (TextView) view.findViewById(R.id.tvName);
            location = (TextView) view.findViewById(R.id.tvLocation);
            details = (TextView) view.findViewById(R.id.tvDetails);
        }
    }
}
