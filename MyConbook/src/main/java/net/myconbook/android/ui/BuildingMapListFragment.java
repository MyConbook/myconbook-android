package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.content.BuildingMaps;
import net.myconbook.android.ui.elements.BuildingMapListItem;
import net.myconbook.android.ui.elements.HotelListItem;

public class BuildingMapListFragment extends ConbookLoaderStandardListFragment<BuildingMapListItem, BuildingMapListItem.Holder> {

    @Override
    protected int getLoaderId() {
        return ConbookLoader.BUILDING_MAPS;
    }

    @Override
    protected StandardCursorAdapter<BuildingMapListItem, BuildingMapListItem.Holder> getAdapter() {
        return new StandardCursorAdapter<BuildingMapListItem, BuildingMapListItem.Holder>(getActivity(), null, android.R.layout.simple_list_item_1) {
            public BuildingMapListItem.Holder createHolder(View view) {
                return new BuildingMapListItem.Holder(view);
            }

            @Override
            public BuildingMapListItem createFromCursor(Cursor cursor) {
                return BuildingMapListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        return new CursorLoader(getActivity(), BuildingMaps.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BuildingMapListItem bmli = (BuildingMapListItem) v.getTag(R.id.data_list_item);
        if (bmli == null) {
            Log.w("BuildingMapListFragment.onListItemClick no view data tag");
            return;
        }

        startFragment(ImageViewFragment.createInstance(bmli.getFilename(), bmli.getName()));
    }
}
