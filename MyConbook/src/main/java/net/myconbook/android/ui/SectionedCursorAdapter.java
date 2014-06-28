package net.myconbook.android.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;

import net.myconbook.android.ui.elements.SectionedListItem;

public abstract class SectionedCursorAdapter<T extends SectionedListItem<H>, H extends SectionedListItem.Holder> extends StandardCursorAdapter<T, H> {
    protected static final int STATE_SECTIONED_CELL = 1;
    protected static final int STATE_REGULAR_CELL = 2;
    protected int[] mCellStates;

    public SectionedCursorAdapter(Activity activity, Cursor c, int resource) {
        super(activity, c, resource);
        mCellStates = c == null ? null : new int[c.getCount()];
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        T listItem = createFromCursor(cursor);

        @SuppressWarnings("unchecked")
        H holder = (H) view.getTag();

        boolean needSeparator;
        final int position = cursor.getPosition();
        String headerValue = null;

        switch (mCellStates[position]) {
            case STATE_SECTIONED_CELL:
                headerValue = getHeader(listItem);
                needSeparator = true;
                break;
            case STATE_REGULAR_CELL:
                needSeparator = false;
                break;
            default:
                headerValue = getHeader(listItem);

                // A separator is needed if it's the first itemview of the
                // ListView or if the group of the current cell is different
                // from the previous itemview.
                if (position == 0) {
                    needSeparator = true;
                } else {
                    cursor.moveToPosition(position - 1);

                    T listItem2 = createFromCursor(cursor);
                    String compValue = getHeader(listItem2);
                    needSeparator = !headerValue.equals(compValue);

                    cursor.moveToPosition(position);
                }

                // Cache the result
                mCellStates[position] = needSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if (needSeparator) {
            holder.header.setText(headerValue);
            holder.header.setVisibility(View.VISIBLE);
        } else {
            holder.header.setVisibility(View.GONE);
        }

        listItem.populateViewHolder(holder);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        mCellStates = newCursor == null ? null : new int[newCursor.getCount()];
        return super.swapCursor(newCursor);
    }

    public abstract String getHeader(T item);
}
