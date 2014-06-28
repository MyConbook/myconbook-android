package net.myconbook.android.content;

import android.net.Uri;

public class Dealers {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/dealers");

    public static final String TABLE_NAME = "dealers";
    public static final String ID = "_id";
    public static final String NAME = "Name";
    public static final String LOCATION = "Location";
    public static final String URL = "URL";
    public static final String DESCRIPTION = "Description";
}
