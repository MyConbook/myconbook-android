package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import net.myconbook.android.R;
import net.myconbook.android.content.Atms;
import net.myconbook.android.ui.elements.AtmListItem;

import java.util.ArrayList;

public class GuideAtmListFragment extends GuideListFragment<AtmListItem, AtmListItem.Holder> {
    @Override
    protected SectionedCursorAdapter<AtmListItem, AtmListItem.Holder> getAdapter() {
        return new SectionedCursorAdapter<AtmListItem, AtmListItem.Holder>(getActivity(), null, R.layout.twolist_item) {
            public String getHeader(AtmListItem item) {
                return item.getName();
            }

            public AtmListItem.Holder createHolder(View view) {
                return new AtmListItem.Holder(view);
            }

            public AtmListItem createFromCursor(Cursor cursor) {
                return AtmListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        mainContentUri = Atms.CONTENT_URI;

        String where = "";
        ArrayList<String> whereArgs = new ArrayList<String>();
        String sortOrder = null;

        if (mSearchText != null && !mSearchText.equals("")) {
            if (!where.equals("")) {
                where += "AND ";
            }

            where += Atms.BANK + " LIKE ? OR " + Atms.BUILDING + " LIKE ?";
            whereArgs.add("%" + mSearchText + "%");
            whereArgs.add("%" + mSearchText + "%");
        }

        String[] whereArgsArray = new String[whereArgs.size()];
        whereArgs.toArray(whereArgsArray);

        return new CursorLoader(getActivity(), mainContentUri, null, where, whereArgsArray, sortOrder);
    }
}
