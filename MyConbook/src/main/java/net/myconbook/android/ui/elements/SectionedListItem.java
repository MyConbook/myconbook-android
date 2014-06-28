package net.myconbook.android.ui.elements;

import android.view.View;
import android.widget.TextView;

import net.myconbook.android.R;

public abstract class SectionedListItem<H extends SectionedListItem.Holder> extends StandardListItem<H> {
    public static class Holder extends StandardListItem.Holder {
        public TextView header;

        public Holder(View view) {
            header = (TextView) view.findViewById(R.id.tvHeader);
        }
    }
}
