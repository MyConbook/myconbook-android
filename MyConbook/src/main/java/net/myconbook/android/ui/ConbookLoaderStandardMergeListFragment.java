package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.commonsware.cwac.merge.MergeAdapter;

import net.myconbook.android.ui.elements.StandardListItem;

public abstract class ConbookLoaderStandardMergeListFragment<L extends StandardListItem<H>, H extends StandardListItem.Holder> extends ConbookListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean mIsActivityCreated;

    protected abstract int getLoaderId();

    protected abstract Loader<Cursor> onCreateLoaderCursor(int id, Bundle args);

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return onCreateLoaderCursor(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        StandardCursorAdapter<L, H> adapter = getStandardAdapterObject();
        adapter.swapCursor(data);

        setListAdapter(getMergeAdapterObject());
        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mStandardAdapter != null) {
            mStandardAdapter.swapCursor(null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIsActivityCreated = true;

        setListShown(false);
        getLoaderManager().initLoader(getLoaderId(), null, this);
    }

    protected void restartLoader() {
        if (mIsActivityCreated) {
            getLoaderManager().restartLoader(getLoaderId(), null, this);
        }
    }

    protected abstract MergeAdapter getMergeAdapter();

    protected abstract StandardCursorAdapter<L, H> getStandardAdapter();

    private MergeAdapter mMergeAdapter;
    private StandardCursorAdapter<L, H> mStandardAdapter;

    protected final MergeAdapter getMergeAdapterObject() {
        if (mMergeAdapter == null) {
            mMergeAdapter = getMergeAdapter();
        }

        return mMergeAdapter;
    }

    protected final StandardCursorAdapter<L, H> getStandardAdapterObject() {
        if (mStandardAdapter == null) {
            mStandardAdapter = getStandardAdapter();
        }

        return mStandardAdapter;
    }
}
