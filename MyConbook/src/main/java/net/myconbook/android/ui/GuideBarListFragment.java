package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import net.myconbook.android.R;
import net.myconbook.android.content.Bars;
import net.myconbook.android.ui.elements.BarListItem;

import java.util.ArrayList;

public class GuideBarListFragment extends GuideListFragment<BarListItem, BarListItem.Holder> {
    @Override
    protected SectionedCursorAdapter<BarListItem, BarListItem.Holder> getAdapter() {
        return new SectionedCursorAdapter<BarListItem, BarListItem.Holder>(getActivity(), null, R.layout.guide_storelistitem) {
            public String getHeader(BarListItem item) {
                return item.getCategory();
            }

            public BarListItem.Holder createHolder(View view) {
                return new BarListItem.Holder(view);
            }

            public BarListItem createFromCursor(Cursor cursor) {
                return BarListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        mainContentUri = Bars.CONTENT_URI;

        String where = "";
        ArrayList<String> whereArgs = new ArrayList<String>();
        String sortOrder = null;

        if (mSearchText != null && !mSearchText.equals("")) {
            if (!where.equals("")) {
                where += "AND ";
            }

            where += Bars.NAME + " LIKE ?";
            whereArgs.add("%" + mSearchText + "%");
        }

        String[] whereArgsArray = new String[whereArgs.size()];
        whereArgs.toArray(whereArgsArray);

        return new CursorLoader(getActivity(), mainContentUri, null, where, whereArgsArray, sortOrder);
    }

}
