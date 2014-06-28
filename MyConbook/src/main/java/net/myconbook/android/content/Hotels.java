package net.myconbook.android.content;

import android.net.Uri;

public class Hotels {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/hotels");

    public static final String TABLE_NAME = "hotels";
    public static final String ID = "_id";
    public static final String NAME = "Name";
    public static final String ADDRESS = "Address";
    public static final String PHONE = "Phone";
    public static final String PLACEID = "PlaceID";
}