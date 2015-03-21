package net.myconbook.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import net.myconbook.android.GuideDestination;
import net.myconbook.android.Log;
import net.myconbook.android.R;

public class GuidePagerFragment extends ConbookPagerFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    protected void createAdapter() {
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                int resId;

                switch (position) {
                    case 0:
                        resId = R.string.restaurantguide;
                        break;
                    case 1:
                        resId = R.string.barguide;
                        break;
                    case 2:
                        resId = R.string.storeguide;
                        break;
                    case 3:
                        resId = R.string.atmguide;
                        break;
                    default:
                        return null;
                }

                return getString(resId);
            }

            @Override
            public Fragment getItem(int position) {
                GuideDestination destination = GuideDestination.Restaurants;

                switch (position) {
                    case 0:
                        destination = GuideDestination.Restaurants;
                        break;
                    case 1:
                        destination = GuideDestination.Bars;
                        break;
                    case 2:
                        destination = GuideDestination.Stores;
                        break;
                    case 3:
                        destination = GuideDestination.ATMs;
                        break;
                }

                return GuideListFragment.createInstance(destination);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_guide, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_report:
                // Handle report
                Log.v("GuidePagerFragment.onOptionsItemSelected report a problem selected");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("plain/text");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_problem));
                startSafeActivity(Intent.createChooser(i, getString(R.string.send_mail)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
