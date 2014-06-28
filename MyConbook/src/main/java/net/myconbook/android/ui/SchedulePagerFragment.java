package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.R;
import net.myconbook.android.content.DayList;
import net.myconbook.android.ui.elements.ScheduleDayListItem;

import java.util.ArrayList;
import java.util.Date;

public class SchedulePagerFragment extends ConbookFragment implements LoaderCallbacks<Cursor> {
    private ProgressBar mProgressBar;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;
    private ArrayList<ScheduleDayListItem> mItems = new ArrayList<ScheduleDayListItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mItems = (ArrayList<ScheduleDayListItem>) savedInstanceState.getSerializable("ScheduleDayList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_loading);

        if (mItems == null || mItems.isEmpty()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        }

        createAdapter();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mItems == null || mItems.isEmpty()) {
            getLoaderManager().initLoader(ConbookLoader.DAYLIST, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("ScheduleDayList", mItems);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DayList.CONTENT_URI, null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateAdapter(data);
        mProgressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_schedule, menu);
    }

    private void createAdapter() {
        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public Fragment getItem(int position) {
                ScheduleDayListItem item = mItems.get(position);
                Date value = item.getCalendar().getTime();
                return ScheduleListFragment.createInstance(value);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                ScheduleDayListItem item = mItems.get(position);
                return item.getNiceName();
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
    }

    private void updateAdapter(final Cursor cursor) {
        if (cursor == null) {
            mPagerAdapter = null;
            mViewPager.setAdapter(null);
            return;
        }

        mItems = new ArrayList<ScheduleDayListItem>();

        while (cursor.moveToNext()) {
            mItems.add(ScheduleDayListItem.createFromCursor(cursor));
        }

        createAdapter();

        // Select today by default
        int current = 0;
        int counter = 0;
        for (ScheduleDayListItem item : mItems) {
            if (item.isToday()) {
                current = counter;
                break;
            }

            counter++;
        }

        mViewPager.setCurrentItem(current);
    }
}
