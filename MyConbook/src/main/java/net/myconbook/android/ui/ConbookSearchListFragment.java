package net.myconbook.android.ui;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.myconbook.android.R;
import net.myconbook.android.ui.elements.SectionedListItem;

/**
 * Created by Andrew on 5/15/2014.
 */
public abstract class ConbookSearchListFragment<L extends SectionedListItem<H>, H extends SectionedListItem.Holder> extends ConbookLoaderSectionedListFragment<L, H> implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    protected abstract void onSearch(String text, boolean submitted);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        MenuItem search = menu.findItem(R.id.menu_search);
        MenuItemCompat.setOnActionExpandListener(search, this);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.filterhint));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem search = menu.findItem(R.id.menu_search);
        if (search != null) {
            MenuItemCompat.collapseActionView(search);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setIconified(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        onSearch(s, true);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        onSearch(s, false);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        onSearch(null, true);
        return true;
    }
}
