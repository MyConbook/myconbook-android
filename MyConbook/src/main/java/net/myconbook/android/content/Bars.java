package net.myconbook.android.content;

import android.net.Uri;

public class Bars {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/bars");

    public static final String TABLE_NAME = "bars";
    public static final String ID = "_id";
    public static final String NAME = "Name";
    public static final String CATEGORY = "Category";
    public static final String DEFAULT_SORT = CATEGORY + ", " + NAME;
    public static final String ADDRESS = "Address";
    public static final String PHONE = "Phone";
    public static final String COMMENTS = "Comments";
    public static final String URL = "URL";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";
    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";
    public static final String PLACEID = "PlaceID";
    public static final String YELPID = "YelpID";
}
