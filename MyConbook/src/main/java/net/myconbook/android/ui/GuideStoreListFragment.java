package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import net.myconbook.android.R;
import net.myconbook.android.content.Stores;
import net.myconbook.android.ui.elements.StoreListItem;

import java.util.ArrayList;

public class GuideStoreListFragment extends GuideListFragment<StoreListItem, StoreListItem.Holder> {
    @Override
    protected SectionedCursorAdapter<StoreListItem, StoreListItem.Holder> getAdapter() {
        return new SectionedCursorAdapter<StoreListItem, StoreListItem.Holder>(getActivity(), null, R.layout.guide_storelistitem) {
            public String getHeader(StoreListItem item) {
                return item.getCategory();
            }

            public StoreListItem.Holder createHolder(View view) {
                return new StoreListItem.Holder(view);
            }

            public StoreListItem createFromCursor(Cursor cursor) {
                return StoreListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        mainContentUri = Stores.CONTENT_URI;

        String where = "";
        ArrayList<String> whereArgs = new ArrayList<String>();
        String sortOrder = null;

        if (mSearchText != null && !mSearchText.equals("")) {
            where = Stores.NAME + " LIKE ?";
            whereArgs.add("%" + mSearchText + "%");
        }

        String[] whereArgsArray = new String[whereArgs.size()];
        whereArgs.toArray(whereArgsArray);

        return new CursorLoader(getActivity(), mainContentUri, null, where, whereArgsArray, sortOrder);
    }
}
