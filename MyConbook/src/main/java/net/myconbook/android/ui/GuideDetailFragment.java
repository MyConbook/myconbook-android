package net.myconbook.android.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.myconbook.android.ConbookLoader;
import net.myconbook.android.GuideDestination;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.RestaurantOpenStatus;
import net.myconbook.android.ui.elements.AtmListItem;
import net.myconbook.android.ui.elements.BarListItem;
import net.myconbook.android.ui.elements.GuideBaseItem;
import net.myconbook.android.ui.elements.RestaurantListItem;
import net.myconbook.android.ui.elements.StoreListItem;

import java.util.ArrayList;

public class GuideDetailFragment extends ConbookListFragment implements LoaderCallbacks<Cursor> {
    private static final int ROW_NAME = 1;
    private static final int ROW_PHONE = 2;
    private static final int ROW_CATEGORY = 3;
    private static final int ROW_RATING = 4;
    private static final int ROW_COST = 5;
    private static final int ROW_DELIVERY = 6;
    private static final int ROW_COMMENTS = 7;
    private static final int ROW_HOURS = 8;
    private static final int ROW_URL = 9;
    private static final int ROW_GOOGLE = 10;
    private static final int ROW_YELP = 11;

    private GuideDestination mDestination;

    private GuideBaseItem<?> item;
    private RestaurantListItem rItem;
    private AtmListItem aItem;
    private ArrayAdapter<Integer> mAdapter;

    public static GuideDetailFragment createInstance(GuideDestination destination, String uri) {
        GuideDetailFragment fragment = new GuideDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("Destination", destination.ordinal());
        bundle.putString("ContentURI", uri);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDestination = GuideDestination.values()[getArguments().getInt("Destination")];
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle b = getArguments();
        getLoaderManager().initLoader(ConbookLoader.GUIDE_DETAIL, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        setListShown(false);
        return new CursorLoader(getActivity(), Uri.parse(args.getString("ContentURI")), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        data.moveToFirst();

        item = getGenericItemFromCursor(data);

        if ((mDestination == GuideDestination.Restaurants) || (mDestination == GuideDestination.RestaurantsOpenNow))
            rItem = RestaurantListItem.createFromCursor(data, (mDestination == GuideDestination.RestaurantsOpenNow));
        else if (mDestination == GuideDestination.ATMs)
            aItem = AtmListItem.createFromCursor(data);

        if (item == null) {
            Log.w("GuideDetailFragment.onLoadFinished no data loaded from cursor");
            Toast.makeText(getActivity(), "Error: No data was loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configure adapter
        ArrayList<Integer> listItems = new ArrayList<Integer>();
        listItems.add(ROW_NAME);

        if (item.getPhone() != null)
            listItems.add(ROW_PHONE);

        listItems.add(ROW_CATEGORY);

        if (rItem != null) {
            if (rItem.getRating() > 0f) {
                listItems.add(ROW_RATING);
            }
            if (rItem.getDollars() > 0) {
                listItems.add(ROW_COST);
            }
            if (rItem.hasDelivery()) {
                listItems.add(ROW_DELIVERY);
            }
        }

        if (item.getComments() != null)
            listItems.add(ROW_COMMENTS);

        if (item.hasHours())
            listItems.add(ROW_HOURS);

        if (item.getURL() != null)
            listItems.add(ROW_URL);

        if (item.getPlaceId() != null)
            listItems.add(ROW_GOOGLE);

        if (item.getYelpId() != null)
            listItems.add(ROW_YELP);

        final LayoutInflater mInflater = getActivity().getLayoutInflater();

        mAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_list_item_1, listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;

                Integer rowType = getItem(position);

                // Get layout type
                int layoutType = android.R.layout.simple_list_item_1;

                if (rowType == ROW_NAME || rowType == ROW_CATEGORY || rowType == ROW_RATING || rowType == ROW_COST || rowType == ROW_COMMENTS) {
                    layoutType = android.R.layout.simple_list_item_2;
                } else if (rowType == ROW_HOURS) {
                    layoutType = R.layout.guide_hoursdetail;
                }

                // Always reinflate layout
                row = mInflater.inflate(layoutType, parent, false);

                // Collect outputs
                TextView text1 = (TextView) row.findViewById(android.R.id.text1);
                TextView text2 = (TextView) row.findViewById(android.R.id.text2);

                StringBuilder sb = new StringBuilder();

                // Output data
                switch (rowType) {
                    case ROW_NAME:
                        text1.setText(item.getName());
                        sb.append(item.getAddress());

                        if (aItem != null) {
                            if (!aItem.getName().equals(aItem.getBuilding())) {
                                sb.append("\r\n").append(aItem.getBuilding());
                            }
                        }

                        text2.setText(sb);
                        break;
                    case ROW_PHONE:
                        text1.setText("Call " + item.getPhone());
                        text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_call, 0);
                        break;
                    case ROW_CATEGORY:
                        text1.setText("Category");
                        text2.setText(item.getCategory());
                        break;
                    case ROW_RATING:
                        text1.setText("Rating");
                        text2.setText(rItem.getRating() + " stars");
                        break;
                    case ROW_COST:
                        text1.setText("Cost");
                        text2.setText(rItem.getDollarsAsSigns());
                        break;
                    case ROW_DELIVERY:
                        text1.setText("Has delivery");
                        break;
                    case ROW_COMMENTS:
                        text1.setText("Review/comments");
                        text2.setText(item.getComments());
                        break;
                    case ROW_HOURS:
                        TextView tvThursday = (TextView) row.findViewById(R.id.tvThursday);
                        TextView tvFriday = (TextView) row.findViewById(R.id.tvFriday);
                        TextView tvSaturday = (TextView) row.findViewById(R.id.tvSaturday);
                        TextView tvSunday = (TextView) row.findViewById(R.id.tvSunday);

                        tvThursday.setText(GuideBaseItem.getTimeText(item.getThursdayHours()));
                        tvFriday.setText(GuideBaseItem.getTimeText(item.getFridayHours()));
                        tvSaturday.setText(GuideBaseItem.getTimeText(item.getSaturdayHours()));
                        tvSunday.setText(GuideBaseItem.getTimeText(item.getSundayHours()));
                        break;
                    case ROW_URL:
                        text1.setText("Open website");
                        text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_directions, 0);
                        break;
                    case ROW_GOOGLE:
                        text1.setText("Open in Google Maps");
                        text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_mapmode, 0);
                        break;
                    case ROW_YELP:
                        text1.setText("Open on Yelp");
                        text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_directions, 0);
                        break;
                    default:
                        break;
                }

                return row;
            }

            public boolean isEnabled(int position) {
                Integer rowType = getItem(position);
                return ((rowType == ROW_PHONE) || (rowType == ROW_URL) || (rowType == ROW_GOOGLE) || (rowType == ROW_YELP));
            }

        };

        setListAdapter(mAdapter);
        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Integer rowType = mAdapter.getItem(position);

        switch (rowType) {
            case ROW_PHONE:
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getPhone()));
                startSafeActivity(i);
                break;
            case ROW_URL:
                launchBrowser(item.getURL());
                break;
            case ROW_GOOGLE:
                showLocation(item);
                break;
            case ROW_YELP:
                launchBrowser("http://www.yelp.com/biz/" + item.getYelpId());
                break;
            default:
                break;
        }
    }

    private void showError(String message) {
        Log.w("GuideDetailFragment.showError displaying error dialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(message)
                .setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showLocation(final GuideBaseItem<?> item) {
        if (item.getPlaceId().equals("")) {
            String message = "This location could not be found in Google Maps.";

            if ((mDestination == GuideDestination.Restaurants) || (mDestination == GuideDestination.RestaurantsOpenNow)) {
                // Special 'closed' checks
                RestaurantListItem restaurant = (RestaurantListItem) item;
                RestaurantOpenStatus openStatus = restaurant.getOpenStatus();

                if (openStatus == RestaurantOpenStatus.Closed) {
                    message = "This location could not be found in Google Maps, and may be closed.";
                } else if (openStatus == RestaurantOpenStatus.VerifiedOpen) {
                    message = "This location could not be found in Google Maps, but it was verified to be open by MyConbook.";
                }
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(true).setTitle("Location not found").setMessage(message)
                    .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        } else {
            if ((mDestination == GuideDestination.Restaurants) || (mDestination == GuideDestination.RestaurantsOpenNow)) {
                // Special 'closed' checks
                RestaurantListItem restaurant = (RestaurantListItem) item;
                RestaurantOpenStatus openStatus = restaurant.getOpenStatus();

                if (openStatus == RestaurantOpenStatus.Closed) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(true).setTitle("Location may be closed")
                            .setMessage("This location was marked as closed according to Google, but was still in the conbook. It may or may not still be open.")
                            .setPositiveButton("Continue", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showPlaceInMaps(item);
                                }
                            })
                            .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();

                    return;
                }
            }

            showPlaceInMaps(item);
        }
    }

    private void showPlaceInMaps(GuideBaseItem<?> item) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://maps.google.com/maps/place?cid=" + item.getPlaceId()));

        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Log.w("GuideDetailFragment.showPlaceInMaps error launching Places link", e);
            showError("Unable to launch Google Maps.");
        }
    }

    private void launchBrowser(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));

        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Log.w("GuideDetailFragment.launchBrowser error launching browser for url " + url, e);
            showError("Unable to launch browser for link " + url + ".");
        }
    }

    private GuideBaseItem<?> getGenericItemFromCursor(Cursor cursor) {
        switch (mDestination) {
            case Restaurants:
            case RestaurantsOpenNow:
                return RestaurantListItem.createFromCursor(cursor, (mDestination == GuideDestination.RestaurantsOpenNow));
            case Bars:
                return BarListItem.createFromCursor(cursor);
            case Stores:
                return StoreListItem.createFromCursor(cursor);
            case ATMs:
                return AtmListItem.createFromCursor(cursor);
            default:
                Log.w("GuideDetailFragment.getGenericItemFromCursor invalid destination " + mDestination + " specified.");
                return null;
        }
    }
}