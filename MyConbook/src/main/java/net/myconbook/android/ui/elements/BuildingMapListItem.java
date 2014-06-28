package net.myconbook.android.ui.elements;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.content.BuildingMaps;

public class BuildingMapListItem extends StandardListItem<BuildingMapListItem.Holder> {
    private String _name;
    private String _filename;

    public BuildingMapListItem(String name, String filename) {
        _name = name;
        _filename = filename;
    }

    public static BuildingMapListItem createFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(BuildingMaps.NAME));
        String filename = c.getString(c.getColumnIndexOrThrow(BuildingMaps.FILENAME));

        return new BuildingMapListItem(name, filename);
    }

    public String getName() {
        return _name;
    }

    public String getFilename() {
        return _filename;
    }

    public void populateViewHolder(Holder holder) {
        holder.name.setText(getName());
    }

    public static class Holder extends StandardListItem.Holder {
        TextView name;

        public Holder(View view) {
            name = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}