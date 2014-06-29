package net.myconbook.android.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.content.Hotels;
import net.myconbook.android.ui.elements.HotelListItem;
import net.myconbook.android.ui.elements.ScheduleListItem;

public class HotelListFragment extends ConbookLoaderStandardListFragment<HotelListItem, HotelListItem.Holder> implements LoaderCallbacks<Cursor> {
    @Override
    protected int getLoaderId() {
        return ConbookLoader.HOTELS;
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        return new CursorLoader(getActivity(), Hotels.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected StandardCursorAdapter<HotelListItem, HotelListItem.Holder> getAdapter() {
        return new StandardCursorAdapter<HotelListItem, HotelListItem.Holder>(getActivity(), null, android.R.layout.simple_list_item_2) {
            public HotelListItem.Holder createHolder(View view) {
                return new HotelListItem.Holder(view);
            }

            @Override
            public HotelListItem createFromCursor(Cursor cursor) {
                return HotelListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final HotelListItem hli = (HotelListItem) v.getTag(R.id.data_list_item);
        if (hli == null) {
            Log.w("HotelListFragment.onListItemClick no view data tag");
            return;
        }

        String items[] = {"Call " + hli.getPhone(), "Open in Google Maps"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(true).setTitle(hli.getName()).setItems(items, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Call
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hli.getPhone()));

                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        showError("Could not find an application to handle the request.");
                    }
                } else if (which == 1) {
                    // Open place
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps/place?cid=" + hli.getPlaceId()));

                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        showError("Could not find an application to handle the request.");
                    }
                } else {
                    dialog.cancel();
                }
            }
        }).show();
    }

    private void showError(String message) {
        Log.w("HotelListFragment.showError displaying error dialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
