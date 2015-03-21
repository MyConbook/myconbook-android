package net.myconbook.android.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.content.ConInfo;
import net.myconbook.android.ui.elements.BuildingMapListItem;
import net.myconbook.android.ui.elements.ConInfoListItem;

public class ConInfoListFragment extends ConbookLoaderStandardMergeListFragment<ConInfoListItem, ConInfoListItem.Holder> {
    @Override
    protected int getLoaderId() {
        return ConbookLoader.CONINFO;
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        return new CursorLoader(getActivity(), ConInfo.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected MergeAdapter getMergeAdapter() {
        MergeAdapter adapter = new MergeAdapter();
        View hotelRow = getMainActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        TextView text1 = (TextView) hotelRow.findViewById(android.R.id.text1);
        text1.setText(R.string.hotel_info);
        adapter.addView(hotelRow, true);

        adapter.addAdapter(getStandardAdapterObject());
        return adapter;
    }

    @Override
    protected StandardCursorAdapter<ConInfoListItem, ConInfoListItem.Holder> getStandardAdapter() {
        return new StandardCursorAdapter<ConInfoListItem, ConInfoListItem.Holder>(getActivity(), null, R.layout.coninfo_item) {
            public ConInfoListItem.Holder createHolder(View view) {
                return new ConInfoListItem.Holder(view);
            }

            @Override
            public ConInfoListItem createFromCursor(Cursor cursor) {
                return ConInfoListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            startFragment(new HotelListFragment());
        } else {
            ConInfoListItem cili = (ConInfoListItem) v.getTag(R.id.data_list_item);
            if (cili == null) {
                Log.w("ConInfoListFragment.onListItemClick no view data tag");
                return;
            }
            startFragment(ImageViewFragment.createInstance(cili.getMapName(), cili.getLocation()));
        }
    }
}
