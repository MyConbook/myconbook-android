package net.myconbook.android.content;

import android.net.Uri;

public class Schedule {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/schedule");

    public static final String TABLE_NAME = "schedule";
    public static final String ID = "_id";
    public static final String TITLE = "Title";
    public static final String DESC = "Description";
    public static final String CATEGORY = "Category";
    public static final String LOCATION = "Location";
    public static final String START = "StartDate";
    public static final String SORTBY_TIME = START + ", " + TITLE;
    public static final String SORTBY_NAME = TITLE + ", " + START;
    public static final String SORTBY_CATEGORY = CATEGORY + ", " + TITLE + ", " + START;
    public static final String SORTBY_ROOM = LOCATION + ", " + START + ", " + TITLE;
    public static final String END = "EndDate";
}