package net.myconbook.android.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import net.myconbook.android.CalendarSortOrder;
import net.myconbook.android.ConbookLoader;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.content.Schedule;
import net.myconbook.android.ui.elements.ScheduleListItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class ScheduleListFragment extends ConbookSearchListFragment<ScheduleListItem, ScheduleListItem.Holder> {
    private Date mStartDateRange;
    private CalendarSortOrder mCalendarSortOrder;
    private String mSearchText;

    public static ScheduleListFragment createInstance(Date date) {
        ScheduleListFragment fragment = new ScheduleListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong("date", date.getTime());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStartDateRange = new Date(getArguments().getLong("date"));
        }

        if (savedInstanceState != null) {
            mCalendarSortOrder = CalendarSortOrder.values()[savedInstanceState.getInt("CSO")];
            Log.v("ScheduleListFragment.onCreate loading existing sort order of " + mCalendarSortOrder);
        } else {
            mCalendarSortOrder = CalendarSortOrder.Time;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(getListView());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CSO", mCalendarSortOrder.ordinal());
    }

    private MenuItem mnuSortTime;
    private MenuItem mnuSortName;
    private MenuItem mnuSortRoom;
    private MenuItem mnuSortGroup;

    private void refreshMenu() {
        if (mnuSortTime == null || mnuSortName == null || mnuSortRoom == null || mnuSortGroup == null) {
            return;
        }

        mnuSortTime.setEnabled(mCalendarSortOrder != CalendarSortOrder.Time);
        mnuSortName.setEnabled(mCalendarSortOrder != CalendarSortOrder.Name);
        mnuSortRoom.setEnabled(mCalendarSortOrder != CalendarSortOrder.Room);
        mnuSortGroup.setEnabled(mCalendarSortOrder != CalendarSortOrder.Category);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (menuVisible) {
            refreshMenu();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mnuSortTime = menu.findItem(R.id.menu_sort_time);
        mnuSortName = menu.findItem(R.id.menu_sort_name);
        mnuSortRoom = menu.findItem(R.id.menu_sort_room);
        mnuSortGroup = menu.findItem(R.id.menu_sort_group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_time:
                mCalendarSortOrder = CalendarSortOrder.Time;
                restartLoader();
                return true;
            case R.id.menu_sort_name:
                mCalendarSortOrder = CalendarSortOrder.Name;
                restartLoader();
                return true;
            case R.id.menu_sort_group:
                mCalendarSortOrder = CalendarSortOrder.Category;
                restartLoader();
                return true;
            case R.id.menu_sort_room:
                mCalendarSortOrder = CalendarSortOrder.Room;
                restartLoader();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        android.view.MenuInflater inflater = getMainActivity().getMenuInflater();
        inflater.inflate(R.menu.fragment_schedule_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info;
        try {
            info = (AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("ScheduleListFragment.onContextItemSelected casting getMenuInfo error", e);
            Log.c(e);
            return false;
        }

        ScheduleListItem cli = (ScheduleListItem) info.targetView.getTag(R.id.data_list_item);
        if (cli == null) {
            Log.w("ScheduleListFragment.onContextItemSelected no target view data tag");
            return false;
        }

        switch (item.getItemId()) {
            case R.id.menu_ctx_addcal:
                Calendar startTime = Calendar.getInstance();
                startTime.setTime(cli.getStartTime());
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(cli.getEndTime());

                Intent i = new Intent(Intent.ACTION_EDIT);
                i.setType("vnd.android.cursor.item/event");
                i.putExtra("beginTime", startTime.getTimeInMillis());
                i.putExtra("endTime", endTime.getTimeInMillis());
                i.putExtra("title", cli.getName());
                i.putExtra("description", cli.getDetails());
                i.putExtra("eventLocation", cli.getRoom());

                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    showError("Could not find an application to handle the request.");
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected int getLoaderId() {
        return ConbookLoader.SCHEDULE;
    }

    @Override
    protected void restartLoader() {
        super.restartLoader();
        refreshMenu();
    }

    @Override
    protected SectionedCursorAdapter<ScheduleListItem, ScheduleListItem.Holder> getAdapter() {
        return new SectionedCursorAdapter<ScheduleListItem, ScheduleListItem.Holder>(getActivity(), null, R.layout.guide_threelistitem) {
            private SimpleDateFormat headerDisplay = new SimpleDateFormat("EEEE 'at' h:mma");

            public String getHeader(ScheduleListItem sli) {
                switch (mCalendarSortOrder) {
                    case Name:
                        return sli.getName().substring(0, 1);
                    case Time:
                        return headerDisplay.format(sli.getStartTime());
                    case Room:
                        return sli.getRoom();
                    case Category:
                        return sli.getCategory();
                    default:
                        return null;
                }
            }

            public ScheduleListItem.Holder createHolder(View view) {
                return new ScheduleListItem.Holder(view);
            }

            public ScheduleListItem createFromCursor(Cursor cursor) {
                return ScheduleListItem.createFromCursor(cursor);
            }
        };
    }

    @Override
    protected Loader<Cursor> onCreateLoaderCursor(int id, Bundle args) {
        String sortBy;

        switch (mCalendarSortOrder) {
            case Name:
                sortBy = Schedule.SORTBY_NAME;
                break;
            case Time:
                sortBy = Schedule.SORTBY_TIME;
                break;
            case Room:
                sortBy = Schedule.SORTBY_ROOM;
                break;
            case Category:
                sortBy = Schedule.SORTBY_CATEGORY;
                break;
            default:
                return null;
        }

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(mStartDateRange);
        calStart.set(Calendar.HOUR_OF_DAY, 4); // Start at 4AM

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(mStartDateRange);
        calEnd.add(Calendar.DATE, 1);
        calEnd.set(Calendar.HOUR_OF_DAY, 4);
        calEnd.add(Calendar.SECOND, -1); // End at +1d@3:59:59 AM

        String startDateRange = iso8601Format.format(calStart.getTime());
        String endDateRange = iso8601Format.format(calEnd.getTime());

        String where = Schedule.START + " >= ? AND " + Schedule.START + " < ?";
        String[] whereArgs;

        if (mSearchText != null && !mSearchText.equals("")) {
            where += " AND " + Schedule.TITLE + " LIKE ?";
            whereArgs = new String[]{startDateRange, endDateRange, "%" + mSearchText + "%"};
        } else {
            whereArgs = new String[]{startDateRange, endDateRange};
        }

        return new CursorLoader(getActivity(), Schedule.CONTENT_URI, null, where, whereArgs, sortBy);
    }

    @Override
    protected void onSearch(String text, boolean submitted) {
        mSearchText = text;
        restartLoader();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ScheduleListItem cli = (ScheduleListItem) v.getTag(R.id.data_list_item);
        if (cli == null) {
            Log.w("ScheduleListFragment.onListItemClick no view data tag");
            return;
        }

        String details = "";

        if (cli.getDetails() != null) {
            details = Html.fromHtml(cli.getDetails()).toString();
        }

        if (details.trim().equals("")) {
            details = "(No description given)";
        }

        if (cli.getCategory() != null && !"".equals(cli.getCategory())) {
            details += "\n\nCategory: " + cli.getCategory();
        }

        // Show details dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(true).setTitle(cli.getName()).setMessage(details).show();
    }

    private void showError(String message) {
        Log.w("ScheduleListFragment.showError displaying error dialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
