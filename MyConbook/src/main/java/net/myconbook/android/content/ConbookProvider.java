package net.myconbook.android.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import net.myconbook.android.Log;

public class ConbookProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "net.myconbook.android.conbookprovider";
    public static final String DATABASE_NAME = "conbook.sqlite";

    private static final int DAYLIST = 1;
    private static final int DAYLIST_ID = 2;
    private static final int SCHEDULE = 10;
    private static final int SCHEDULE_ID = 11;
    private static final int DEALERS = 20;
    private static final int DEALER_ID = 21;
    private static final int RESTAURANTS = 30;
    private static final int RESTAURANT_ID = 31;
    private static final int RESTAURANT_CATEGORIES = 32;
    private static final int BARS = 40;
    private static final int BAR_ID = 41;
    private static final int STORES = 50;
    private static final int STORE_ID = 51;
    private static final int ATMS = 60;
    private static final int ATM_ID = 61;
    private static final int CONINFO = 70;
    private static final int CONINFO_ID = 71;
    private static final int HOTELS = 80;
    private static final int HOTELS_ID = 81;
    private static final int BUILDING_MAPS = 90;
    private static final int BUILDING_MAPS_ID = 91;

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(PROVIDER_NAME, "daylist", DAYLIST);
        matcher.addURI(PROVIDER_NAME, "daylist/#", DAYLIST_ID);
        matcher.addURI(PROVIDER_NAME, "schedule", SCHEDULE);
        matcher.addURI(PROVIDER_NAME, "schedule/#", SCHEDULE_ID);
        matcher.addURI(PROVIDER_NAME, "dealers", DEALERS);
        matcher.addURI(PROVIDER_NAME, "dealers/#", DEALER_ID);
        matcher.addURI(PROVIDER_NAME, "restaurants", RESTAURANTS);
        matcher.addURI(PROVIDER_NAME, "restaurants/#", RESTAURANT_ID);
        matcher.addURI(PROVIDER_NAME, "restaurants/categories", RESTAURANT_CATEGORIES);
        matcher.addURI(PROVIDER_NAME, "bars", BARS);
        matcher.addURI(PROVIDER_NAME, "bars/#", BAR_ID);
        matcher.addURI(PROVIDER_NAME, "stores", STORES);
        matcher.addURI(PROVIDER_NAME, "stores/#", STORE_ID);
        matcher.addURI(PROVIDER_NAME, "atms", ATMS);
        matcher.addURI(PROVIDER_NAME, "atms/#", ATM_ID);
        matcher.addURI(PROVIDER_NAME, "coninfo", CONINFO);
        matcher.addURI(PROVIDER_NAME, "coninfo/#", CONINFO_ID);
        matcher.addURI(PROVIDER_NAME, "hotels", HOTELS);
        matcher.addURI(PROVIDER_NAME, "hotels/#", HOTELS_ID);
        matcher.addURI(PROVIDER_NAME, "buildingmaps", BUILDING_MAPS);
        matcher.addURI(PROVIDER_NAME, "buildingmaps/#", BUILDING_MAPS_ID);
    }

    private Context context;

    @Override
    public boolean onCreate() {
        context = getContext();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (matcher.match(uri)) {
            case DAYLIST:
                builder.setTables(DayList.TABLE_NAME);
                break;
            case DAYLIST_ID:
                builder.setTables(DayList.TABLE_NAME);
                builder.appendWhere(DayList.ID + " = " + uri.getPathSegments().get(1));
                break;
            case SCHEDULE:
                builder.setTables(Schedule.TABLE_NAME);
                break;
            case SCHEDULE_ID:
                builder.setTables(Schedule.TABLE_NAME);
                builder.appendWhere(Schedule.ID + " = " + uri.getPathSegments().get(1));
                break;
            case DEALERS:
                builder.setTables(Dealers.TABLE_NAME);
                break;
            case DEALER_ID:
                builder.setTables(Dealers.TABLE_NAME);
                builder.appendWhere(Dealers.ID + " = " + uri.getPathSegments().get(1));
                break;
            case RESTAURANTS:
                builder.setTables(Restaurants.TABLE_NAME);

                if (sortOrder == null) {
                    sortOrder = Restaurants.DEFAULT_SORT;
                }
                break;
            case RESTAURANT_ID:
                builder.setTables(Restaurants.TABLE_NAME);
                builder.appendWhere(Restaurants.ID + " = " + uri.getPathSegments().get(1));
                break;
            case RESTAURANT_CATEGORIES:
                builder.setTables(RestaurantCategories.TABLE_NAME);
                break;
            case BARS:
                builder.setTables(Bars.TABLE_NAME);

                if (sortOrder == null) {
                    sortOrder = Bars.DEFAULT_SORT;
                }
                break;
            case BAR_ID:
                builder.setTables(Bars.TABLE_NAME);
                builder.appendWhere(Bars.ID + " = " + uri.getPathSegments().get(1));
                break;
            case STORES:
                builder.setTables(Stores.TABLE_NAME);

                if (sortOrder == null) {
                    sortOrder = Stores.DEFAULT_SORT;
                }
                break;
            case STORE_ID:
                builder.setTables(Stores.TABLE_NAME);
                builder.appendWhere(Stores.ID + " = " + uri.getPathSegments().get(1));
                break;
            case ATMS:
                builder.setTables(Atms.TABLE_NAME);
                break;
            case ATM_ID:
                builder.setTables(Atms.TABLE_NAME);
                builder.appendWhere(Atms.ID + " = " + uri.getPathSegments().get(1));
                break;
            case CONINFO:
                builder.setTables(ConInfo.TABLE_NAME);
                break;
            case CONINFO_ID:
                builder.setTables(ConInfo.TABLE_NAME);
                builder.appendWhere(ConInfo.ID + " = " + uri.getPathSegments().get(1));
                break;
            case HOTELS:
                builder.setTables(Hotels.TABLE_NAME);
                break;
            case HOTELS_ID:
                builder.setTables(Hotels.TABLE_NAME);
                builder.appendWhere(Hotels.ID + " = " + uri.getPathSegments().get(1));
                break;
            case BUILDING_MAPS:
                builder.setTables(BuildingMaps.TABLE_NAME);
                break;
            case BUILDING_MAPS_ID:
                builder.setTables(BuildingMaps.TABLE_NAME);
                builder.appendWhere(BuildingMaps.ID + " = " + uri.getPathSegments().get(1));
                break;
            default:
                Log.w("Invalid content URI requested: " + uri);
                return null;
        }

        return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        final String dir = "vnd.android.dir/vnd.myconbook.";
        final String item = "vnd.android.item/vnd.myconbook.";

        switch (matcher.match(uri)) {
            case DAYLIST:
                return dir + "daylist";
            case DAYLIST_ID:
                return item + "daylist";
            case SCHEDULE:
                return dir + "schedule";
            case SCHEDULE_ID:
                return item + "schedule";
            case DEALERS:
                return dir + "dealers";
            case DEALER_ID:
                return item + "dealer";
            case RESTAURANTS:
                return dir + "restaurants";
            case RESTAURANT_ID:
                return item + "restaurant";
            case RESTAURANT_CATEGORIES:
                return dir + "restaurants.categories";
            case BARS:
                return dir + "bars";
            case BAR_ID:
                return item + "bar";
            case STORES:
                return dir + "stores";
            case STORE_ID:
                return item + "store";
            case ATMS:
                return dir + "atms";
            case ATM_ID:
                return item + "atm";
            case CONINFO:
                return dir + "coninfo";
            case CONINFO_ID:
                return item + "coninfo";
            case HOTELS:
                return dir + "hotels";
            case HOTELS_ID:
                return item + "hotels";
            case BUILDING_MAPS:
                return dir + "buildingmaps";
            case BUILDING_MAPS_ID:
                return item + "buildingmaps";
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        throw new UnsupportedOperationException();
    }
}
