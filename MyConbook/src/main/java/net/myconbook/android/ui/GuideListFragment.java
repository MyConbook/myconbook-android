package net.myconbook.android.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.GuideDestination;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.ui.elements.GuideBaseItem;

public abstract class GuideListFragment<L extends GuideBaseItem<H>, H extends GuideBaseItem.Holder> extends ConbookSearchListFragment<L, H> {
    protected GuideDestination mDestination;
    protected Uri mainContentUri;
    private int mListIndex;
    private int mListTop;
    protected String mSearchText;

    public static GuideListFragment createInstance(GuideDestination destination) {
        GuideListFragment fragment;

        switch (destination) {
            case Restaurants:
            case RestaurantsOpenNow:
                fragment = new GuideRestaurantListFragment();
                break;
            case Bars:
                fragment = new GuideBarListFragment();
                break;
            case Stores:
                fragment = new GuideStoreListFragment();
                break;
            case ATMs:
                fragment = new GuideAtmListFragment();
                break;
            default:
                return null;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("Destination", destination.ordinal());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("GuideListFragment.onCreate destination " + mDestination);

        if (getArguments() != null) {
            mDestination = GuideDestination.values()[getArguments().getInt("Destination")];
        }

        if (savedInstanceState != null) {
            mDestination = GuideDestination.values()[savedInstanceState.getInt("Destination")];
            mListIndex = savedInstanceState.getInt("ListIndex", 0);
            mListTop = savedInstanceState.getInt("ListTop", 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListIndex > 0 || mListTop > 0) {
            getListView().setSelectionFromTop(mListIndex, mListTop);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mListIndex = getListView().getFirstVisiblePosition();
        mListTop = getListView().getTop();
    }

    @Override
    protected int getLoaderId() {
        return ConbookLoader.GUIDE;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);

        if (getView() != null) {
            getListView().setSelectionFromTop(mListIndex, mListTop);
        }
    }

    protected void onSearch(String text, boolean submitted) {
        mSearchText = text;
        restartLoader();
    }


    @Override
    protected void restartLoader() {
        super.restartLoader();
        mListIndex = 0;
        mListTop = 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("Destination", mDestination.ordinal());

        if (getView() != null) {
            outState.putInt("ListIndex", getListView().getFirstVisiblePosition());
            outState.putInt("ListTop", getListView().getTop());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean isOpenNow = mDestination == GuideDestination.RestaurantsOpenNow;
        boolean isRestaurant = mDestination == GuideDestination.Restaurants || isOpenNow;

        MenuItem filter = menu.findItem(R.id.menu_filter);
        if (filter != null) {
            filter.setVisible(isRestaurant);
        }

        MenuItem openNow = menu.findItem(R.id.menu_opennow);
        if (openNow != null) {
            openNow.setVisible(isRestaurant);
            openNow.setChecked(isOpenNow);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startFragment(GuideDetailFragment.createInstance(mDestination, mainContentUri.toString() + "/" + id));
    }
}
