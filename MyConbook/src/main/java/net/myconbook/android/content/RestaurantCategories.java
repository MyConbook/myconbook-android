package net.myconbook.android.content;

import android.net.Uri;

public class RestaurantCategories {
    public static final Uri CONTENT_URI = Uri.parse("content://" + ConbookProvider.PROVIDER_NAME + "/restaurants/categories");

    public static final String TABLE_NAME = "restaurantcategories";
    public static final String ID = "_id";
    public static final String CATEGORY = "Category";
}
