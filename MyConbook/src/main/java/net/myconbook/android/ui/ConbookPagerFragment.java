package net.myconbook.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;

import net.myconbook.android.R;

/**
 * Created by Andrew on 11/23/2014.
 */
public abstract class ConbookPagerFragment extends ConbookFragment {
    protected PagerSlidingTabStrip mTabStrip;
    protected ViewPager mViewPager;
    protected ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);

        mTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_loading);

        createAdapter();

        if (!isLoadDelayed()) {
            mTabStrip.setViewPager(mViewPager);
        }

        return view;
    }

    protected abstract void createAdapter();

    protected boolean isLoadDelayed() {
        return false;
    }

    protected void show() {
        mProgressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mTabStrip.setVisibility(View.VISIBLE);
    }

    protected void hide() {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        mTabStrip.setVisibility(View.GONE);
    }
}
