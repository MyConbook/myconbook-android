package net.myconbook.android.ui.elements;

public abstract class StandardListItem<H extends StandardListItem.Holder> {
    public abstract void populateViewHolder(H holder);

    public static class Holder {
    }
}
