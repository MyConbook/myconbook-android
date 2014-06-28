package net.myconbook.android.content;

import android.net.Uri;

public class ConInfo {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/coninfo");

    public static final String TABLE_NAME = "coninfo";
    public static final String ID = "_id";
    public static final String NAME = "Name";
    public static final String LOCATION = "Location";
    public static final String DETAILS = "Details";
    public static final String MAP_NAME = "MapName";
}
