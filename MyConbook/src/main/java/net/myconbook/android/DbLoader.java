package net.myconbook.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.HashMap;

public class DbLoader {
    public static final String DATABASE_NAME = "conbook.sqlite";

    public static final String TABLE_INFO = "info";
    public static final String TABLE_INFO_NAME = "KeyName";
    public static final String TABLE_INFO_VALUE = "KeyValue";

    public static final String INFO_BUILDDATE = "BuildDate";
    public static final String INFO_DBVER = "Version";
    public static final String INFO_CONVENTION = "Convention";
    public static final String INFO_PROVIDER_DETAILS = "ProviderDetails";
    public static final String INFO_AREA_MAP = "AreaMapURL";
    public static final String INFO_HAS_GUIDE = "HasGuide";
    public static final String INFO_GUIDE_URL = "GuideURL";

    private SQLiteDatabase mDatabase;
    private HashMap<String, String> mInfoCache;

    public DbLoader(SQLiteDatabase db) {
        mDatabase = db;
        getInfoCache();
    }

    public boolean isValid() {
        try {
            HashMap<String, String> info = getInfoCache();
            return info.containsKey(INFO_BUILDDATE);
        } catch (SQLiteException e) {
            Log.e("Database validation failure", e);
            Log.c(e);
            return false;
        }
    }

    public int getDbVersion() {
        try {
            HashMap<String, String> info = getInfoCache();
            if (info.containsKey(INFO_DBVER)) {
                return Integer.parseInt(info.get(INFO_DBVER));
            } else {
                return -1;
            }
        } catch (SQLiteException e) {
            Log.e("Error retrieving database version", e);
            Log.c(e);
            return -1;
        }
    }

    private HashMap<String, String> getInfo() throws SQLiteException {
        HashMap<String, String> list = new HashMap<String, String>();

        Cursor c = mDatabase.query(TABLE_INFO, null, null, null, null, null, null);

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndexOrThrow(TABLE_INFO_NAME));
            String value = c.getString(c.getColumnIndexOrThrow(TABLE_INFO_VALUE));

            list.put(name, value);
        }

        c.close();

        return list;
    }

    public HashMap<String, String> getInfoCache() throws SQLiteException {
        if (mInfoCache == null) {
            mInfoCache = getInfo();
        }

        return mInfoCache;
    }
}
