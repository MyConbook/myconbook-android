package net.myconbook.android.ui;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.content.Dealers;
import net.myconbook.android.ui.elements.ConInfoListItem;
import net.myconbook.android.ui.elements.DealerListItem;

public class DealersListFragment extends ConbookSearchListFragment<DealerListItem, DealerListItem.Holder> {
    private String mSearchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dealers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map:
                // TODO: Make this better
                startFragment(ImageViewFragment.createInstance("dealersroom", getString(R.string.dealers_room)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getLoaderId() {
        return ConbookLoader.DEALERS;
    }

    @Override
    protected SectionedCursorAdapter<DealerListItem, DealerListItem.Holder> getAdapter() {
        return new SectionedCursorAdapter<DealerListItem, DealerListItem.Holder>(getActivity(), null, R.layout.twolist_item) {
            @Override
            public String getHeader(DealerListItem item) {
                return item.getName().substring(0, 1);
            }

            public DealerListItem.Holder createHolder(View view) {
                return new DealerListItem.Holder(view);
            }

            @Override
            public DealerListItem createFromCursor(Cursor cursor) {
                return DealerListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        String where = null;
        String[] whereArgs = null;

        if (mSearchText != null) {
            where = Dealers.NAME + " LIKE ?";
            whereArgs = new String[]{"%" + mSearchText + "%"};
        }

        return new CursorLoader(getActivity(), Dealers.CONTENT_URI, null, where, whereArgs, Dealers.NAME);
    }

    @Override
    protected void onSearch(String text, boolean submitted) {
        mSearchText = text;
        restartLoader();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DealerListItem dli = (DealerListItem) v.getTag(R.id.data_list_item);
        if (dli == null) {
            Log.w("DealersListFragment.onListItemClick no view data tag");
            return;
        }

        StringBuilder sb = new StringBuilder();

        String details = dli.getDescription();
        if (details == null) {
            sb.append(getString(R.string.no_description));
        } else {
            sb.append(details);
        }

        String url = dli.getURL();

        if (url != null) {
            sb.append("\r\n").append(url);
        }

        // Show details dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        AlertDialog d = dialog.setCancelable(true).setTitle(dli.getName()).setMessage(sb).show();

        // Parse URLs in field
        TextView message = (TextView) d.findViewById(android.R.id.message);
        Linkify.addLinks(message, Linkify.ALL);
    }
}