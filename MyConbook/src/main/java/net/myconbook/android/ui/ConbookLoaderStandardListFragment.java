package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import net.myconbook.android.ui.elements.StandardListItem;

public abstract class ConbookLoaderStandardListFragment<L extends StandardListItem<H>, H extends StandardListItem.Holder> extends ConbookListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean mIsActivityCreated;

    protected abstract int getLoaderId();

    protected abstract Loader<Cursor> onCreateLoaderCursor(int id, Bundle args);

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        setListShown(false);
        return onCreateLoaderCursor(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        StandardCursorAdapter<L, H> adapter = getAdapterObject();
        adapter.swapCursor(data);
        setListAdapter(adapter);
        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIsActivityCreated = true;
        getLoaderManager().initLoader(getLoaderId(), null, this);
    }

    protected void restartLoader() {
        if (mIsActivityCreated) {
            getLoaderManager().restartLoader(getLoaderId(), null, this);
        }
    }

    protected abstract StandardCursorAdapter<L, H> getAdapter();

    private StandardCursorAdapter<L, H> mAdapter;

    protected final StandardCursorAdapter<L, H> getAdapterObject() {
        if (mAdapter == null) {
            mAdapter = getAdapter();
        }

        return mAdapter;
    }
}
