package net.myconbook.android.ui.elements;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.content.Atms;

public class AtmListItem extends GuideBaseItem<AtmListItem.Holder> {
    private String _building;

    public AtmListItem(String name, String category, String building, String address, String placeId) {
        super(name, category, address, null, null, null, null, null, null, null, placeId, null);

        _building = building;
    }

    public static AtmListItem createFromCursor(Cursor c) {
        String bank = c.getString(c.getColumnIndexOrThrow(Atms.BANK));
        String category = c.getString(c.getColumnIndexOrThrow(Atms.CATEGORY));
        String building = c.getString(c.getColumnIndexOrThrow(Atms.BUILDING));
        String address = c.getString(c.getColumnIndexOrThrow(Atms.ADDRESS));
        String placeId = c.getString(c.getColumnIndexOrThrow(Atms.PLACEID));

        return new AtmListItem(bank, category, building, address, placeId);
    }

    public String getBuilding() {
        return _building;
    }

    public void populateViewHolder(Holder holder) {
        // We use custom fields for ATMs
        holder.address.setText(getAddress());
        holder.building.setText(getBuilding());
    }

    public static class Holder extends GuideBaseItem.Holder {
        TextView address;
        TextView building;

        public Holder(View view) {
            super(view);

            address = (TextView) view.findViewById(android.R.id.text1);
            building = (TextView) view.findViewById(android.R.id.text2);
        }
    }
}
