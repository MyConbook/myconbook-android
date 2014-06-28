package net.myconbook.android.content;

import android.net.Uri;

public class Atms {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/atms");

    public static final String TABLE_NAME = "atms";
    public static final String ID = "_id";
    public static final String BANK = "Name";
    public static final String CATEGORY = "Category";
    public static final String BUILDING = "Building";
    public static final String ADDRESS = "Address";
    public static final String PLACEID = "PlaceID";
}
