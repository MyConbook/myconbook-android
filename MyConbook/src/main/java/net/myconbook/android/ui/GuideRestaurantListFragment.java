package net.myconbook.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.GuideDestination;
import net.myconbook.android.R;
import net.myconbook.android.content.RestaurantCategories;
import net.myconbook.android.content.Restaurants;
import net.myconbook.android.ui.elements.RestaurantListItem;

import java.util.ArrayList;

public class GuideRestaurantListFragment extends GuideListFragment<RestaurantListItem, RestaurantListItem.Holder> {
    private String[] mSectionList;
    private int mSectionFilterPos;
    private String mSectionFilterStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSectionFilterPos = savedInstanceState.getInt("SectionFilterPos");
            mSectionFilterStr = savedInstanceState.getString("SectionFilterStr");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mDestination == GuideDestination.Restaurants || mDestination == GuideDestination.RestaurantsOpenNow) {
            getLoaderManager().initLoader(ConbookLoader.GUIDE_CATEGORY, null, mCategoryLoaderCallbacks);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("SectionFilterPos", mSectionFilterPos);
        outState.putString("SectionFilterStr", mSectionFilterStr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilterDisplay();
                return true;
            case R.id.menu_opennow:
                boolean isOpenNow = mDestination == GuideDestination.RestaurantsOpenNow;
                mDestination = isOpenNow ? GuideDestination.Restaurants : GuideDestination.RestaurantsOpenNow;
                restartLoader();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected SectionedCursorAdapter<RestaurantListItem, RestaurantListItem.Holder> getAdapter() {
        return new SectionedCursorAdapter<RestaurantListItem, RestaurantListItem.Holder>(getActivity(), null, R.layout.guide_restaurantlistitem) {
            public String getHeader(RestaurantListItem item) {
                return item.getCategory();
            }

            public RestaurantListItem.Holder createHolder(View view) {
                return new RestaurantListItem.Holder(view);
            }

            public RestaurantListItem createFromCursor(Cursor cursor) {
                return RestaurantListItem.createFromCursor(cursor, (mDestination == GuideDestination.RestaurantsOpenNow));
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        mainContentUri = Restaurants.CONTENT_URI;

        String where = "";
        ArrayList<String> whereArgs = new ArrayList<String>();
        String sortOrder = null;

        if (mDestination == GuideDestination.RestaurantsOpenNow) {
            where = Restaurants.ISOPEN + " = 1";
        }

        if (mSectionFilterStr != null) {
            if (!where.equals("")) {
                where += " AND ";
            }

            where += Restaurants.CATEGORY + " = ?";
            whereArgs.add(mSectionFilterStr);
        }

        if (mSearchText != null && !mSearchText.equals("")) {
            if (!where.equals("")) {
                where += " AND ";
            }

            where += Restaurants.NAME + " LIKE ?";
            whereArgs.add("%" + mSearchText + "%");
        }

        String[] whereArgsArray = new String[whereArgs.size()];
        whereArgs.toArray(whereArgsArray);

        return new CursorLoader(getActivity(), mainContentUri, null, where, whereArgsArray, sortOrder);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mCategoryLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), RestaurantCategories.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null) return;

            ArrayList<String> categories = new ArrayList<String>();
            categories.add(" --- All --- ");

            while (data.moveToNext()) {
                categories.add(data.getString(data.getColumnIndexOrThrow(RestaurantCategories.CATEGORY)));
            }

            mSectionList = new String[categories.size()];
            categories.toArray(mSectionList);
            return;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private void showFilterDisplay() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(true).setTitle(R.string.filtercategory)
                .setSingleChoiceItems(mSectionList, mSectionFilterPos, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSectionFilterPos = which;
                        mSectionFilterStr = (which > 0) ? mSectionList[which] : null;
                        dialog.dismiss();

                        restartLoader();
                    }
                }).show();
    }

}
