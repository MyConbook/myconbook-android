package net.myconbook.android.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.myconbook.android.ui.elements.StandardListItem;

public abstract class StandardCursorAdapter<T extends StandardListItem<H>, H extends StandardListItem.Holder> extends android.support.v4.widget.CursorAdapter {
    private LayoutInflater mInflater;
    private int mResource;

    public StandardCursorAdapter(Activity activity, Cursor c, int resource) {
        super(activity, c, 0);
        mInflater = activity.getLayoutInflater();
        mResource = resource;
    }

    public abstract H createHolder(View view);

    public abstract T createFromCursor(Cursor cursor);

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(mResource, parent, false);
        H holder = createHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        T listItem = createFromCursor(cursor);

        @SuppressWarnings("unchecked")
        H holder = (H) view.getTag();

        listItem.populateViewHolder(holder);
    }
}
