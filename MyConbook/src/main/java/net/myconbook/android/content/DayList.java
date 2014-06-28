package net.myconbook.android.content;

import android.net.Uri;

public class DayList {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/daylist");

    public static final String TABLE_NAME = "daylist";
    public static final String ID = "_id";
    public static final String DAY = "Day";
}