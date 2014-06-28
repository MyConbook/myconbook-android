package net.myconbook.android.content;

import android.net.Uri;

public class BuildingMaps {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/buildingmaps");

    public static final String TABLE_NAME = "buildingmaps";
    public static final String ID = "_id";
    public static final String NAME = "Name";
    public static final String FILENAME = "Filename";
}