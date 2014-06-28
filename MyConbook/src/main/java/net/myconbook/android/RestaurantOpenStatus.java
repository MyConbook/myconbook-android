package net.myconbook.android;

/**
 * Track the status of a restaurant, according to Google Maps.
 */
public enum RestaurantOpenStatus {
    /**
     * Restaurant is open. If missing the PlaceID, then the address is unverified, and may be closed.
     */
    Open,

    /**
     * Restaurant is closed, according to Google.
     */
    Closed,

    /**
     * Restaurant was personally verified as being open and at the given location, but could not be found in Google.
     */
    VerifiedOpen,
}
