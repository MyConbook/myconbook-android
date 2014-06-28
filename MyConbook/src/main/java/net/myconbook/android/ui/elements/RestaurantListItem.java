package net.myconbook.android.ui.elements;

import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import net.myconbook.android.R;
import net.myconbook.android.RestaurantOpenStatus;
import net.myconbook.android.content.Restaurants;

public class RestaurantListItem extends GuideBaseItem<RestaurantListItem.Holder> {
    private float _rating;
    private int _dollars;
    private boolean _isOpen;
    private boolean _hasHours;
    private boolean _delivery;
    private RestaurantOpenStatus _openStatus;
    private boolean _isOpenDisplayed;

    public RestaurantListItem(String name, String category, float rating, int dollars, String address, String phone, String comments, String url, String thursday, String friday, String saturday, String sunday, boolean isOpen, boolean hasHours, boolean delivery, RestaurantOpenStatus openStatus, String placeId, String yelpId) {
        super(name, category, address, phone, comments, url, thursday, friday, saturday, sunday, placeId, yelpId);

        _rating = rating;
        _dollars = dollars;
        _isOpen = isOpen;
        _hasHours = hasHours;
        _delivery = delivery;
        _openStatus = openStatus;
    }

    public static RestaurantListItem createFromCursor(Cursor c, boolean openNow) {
        String name = c.getString(c.getColumnIndexOrThrow(Restaurants.NAME));
        String category = c.getString(c.getColumnIndexOrThrow(Restaurants.CATEGORY));
        float rating = c.getFloat(c.getColumnIndexOrThrow(Restaurants.RATING));
        int dollars = c.getInt(c.getColumnIndexOrThrow(Restaurants.DOLLARS));
        String address = c.getString(c.getColumnIndexOrThrow(Restaurants.ADDRESS));
        String phone = c.getString(c.getColumnIndexOrThrow(Restaurants.PHONE));
        String comments = c.getString(c.getColumnIndexOrThrow(Restaurants.COMMENTS));
        String url = c.getString(c.getColumnIndexOrThrow(Restaurants.URL));
        String thursday = c.getString(c.getColumnIndexOrThrow(Restaurants.THURSDAY));
        String friday = c.getString(c.getColumnIndexOrThrow(Restaurants.FRIDAY));
        String saturday = c.getString(c.getColumnIndexOrThrow(Restaurants.SATURDAY));
        String sunday = c.getString(c.getColumnIndexOrThrow(Restaurants.SUNDAY));
        boolean isOpen = (c.getInt(c.getColumnIndexOrThrow(Restaurants.ISOPEN)) > 0);
        boolean hasHours = (c.getInt(c.getColumnIndexOrThrow(Restaurants.HASHOURS)) > 0);
        boolean delivery = (c.getInt(c.getColumnIndexOrThrow(Restaurants.DELIVERY)) > 0);
        RestaurantOpenStatus openStatus = RestaurantOpenStatus.values()[c.getInt(c.getColumnIndexOrThrow(Restaurants.CLOSED))];
        String placeId = c.getString(c.getColumnIndexOrThrow(Restaurants.PLACEID));
        String yelpId = c.getString(c.getColumnIndexOrThrow(Restaurants.YELPID));

        RestaurantListItem restaurant = new RestaurantListItem(name, category, rating, dollars, address, phone, comments, url, thursday, friday, saturday, sunday, isOpen, hasHours, delivery, openStatus, placeId, yelpId);

        // Disable highlight if opened from Open Now page
        restaurant.setIsOpenDisplayed(!openNow);

        return restaurant;
    }

    public float getRating() {
        return _rating;
    }

    public int getDollars() {
        return _dollars;
    }

    public String getDollarsAsSigns() {
        if (getDollars() == 1) return "$";
        if (getDollars() == 2) return "$$";
        if (getDollars() == 3) return "$$$";
        if (getDollars() == 4) return "$$$$";
        if (getDollars() >= 5) return "$$$$$";

        return "";
    }

    public boolean isOpen() {
        return _isOpen;
    }

    public boolean hasHours() {
        return _hasHours;
    }

    public boolean hasDelivery() {
        return _delivery;
    }

    public RestaurantOpenStatus getOpenStatus() {
        return _openStatus;
    }

    public boolean isOpenDisplayed() {
        return _isOpenDisplayed;
    }

    public void setIsOpenDisplayed(boolean value) {
        _isOpenDisplayed = value;
    }

    public void populateViewHolder(Holder holder) {
        super.populateViewHolderParent(holder);

        // Redo name
        String name = getName();
        if (getOpenStatus() == RestaurantOpenStatus.Closed) {
            name += " [Closed]";
        }

        holder.name.setText(name);

        String dollars = "";
        if (hasDelivery()) dollars += "D";
        if (!dollars.equals("")) dollars += " ";

        holder.dollars.setText(dollars + getDollarsAsSigns());

        // Rating is removed until we need it again and can fix the layout
        /*float rating = getRating();

		if (rating > 0) {
			holder.rating.setRating(rating);
			holder.rating.setVisibility(View.VISIBLE);
		}
		else {
			holder.rating.setVisibility(View.INVISIBLE);
		}*/

        if ((!holder.hours.getText().toString().equals("Not available")) && isOpenDisplayed() && hasHours() && isOpen() && (getOpenStatus() != RestaurantOpenStatus.Closed)) {
            holder.hours.setTextColor(Color.GREEN);
        } else {
            holder.hours.setTextColor(holder.address.getTextColors().getDefaultColor());
        }
    }

    public static class Holder extends GuideBaseItem.Holder {
        //RatingBar rating;
        TextView dollars;

        public Holder(View view) {
            super(view);

            //rating = (RatingBar)view.findViewById(R.id.rbRating);
            dollars = (TextView) view.findViewById(R.id.tvDollars);
        }
    }
}
