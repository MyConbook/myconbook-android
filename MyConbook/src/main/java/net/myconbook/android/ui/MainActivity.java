package net.myconbook.android.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.DbLoader;
import net.myconbook.android.Log;
import net.myconbook.android.R;
import net.myconbook.android.UpdateChecker;
import net.myconbook.android.model.UpdaterInfo;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements OnClickListener {
    private HashMap<String, String> mDbInfo;
    private boolean mIsPaused;
    private boolean mAboutShown;
    private boolean mAlertShown;

    private HackyDrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private LinearLayout llUpdateMessage;
    private TextView tvUpdateMessage;
    private Spinner spConSelector;
    private TextView btnSchedule;
    private TextView btnGuide;
    private TextView btnConInfo;
    private TextView btnDealers;
    private TextView btnBuildingMaps;
    private TextView btnAreaMap;

    private UpdateFragment mUpdateFragment;

    private Handler onUpdaterEvent = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("MainActivity.onUpdaterEvent got response from updater: " + msg.what);

            switch (msg.what) {
                case UpdateFragment.FINISH_LIST_OK:
                    populateConList();
                    break;
                case UpdateFragment.FINISH_CON_OK:
                    hideUpdateProgress();
                    refreshFromDb();
                    break;
                case UpdateFragment.FINISH_ERROR:
                    finish();
                    break;
                case UpdateFragment.FINISH_STARTED:
                    showUpdateProgress((String) msg.obj);
                    break;
            }

            return true;
        }
    });

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Crashlytics.start(this);
        }

        setContentView(R.layout.main_activity);
        getSupportActionBar().setTitle(R.string.conname);

        if (savedInstanceState != null) {
            mAboutShown = savedInstanceState.getBoolean("AboutShown");
        }

        llUpdateMessage = (LinearLayout) findViewById(R.id.update_layout);
        tvUpdateMessage = (TextView) findViewById(R.id.update_text);

        spConSelector = (Spinner) findViewById(R.id.con_selector);

        // Register buttons
        btnSchedule = (TextView) findViewById(R.id.schedule_button);
        btnSchedule.setOnClickListener(this);

        btnGuide = (TextView) findViewById(R.id.guide_button);
        btnGuide.setOnClickListener(this);

        btnConInfo = (TextView) findViewById(R.id.con_info_button);
        btnConInfo.setOnClickListener(this);

        btnDealers = (TextView) findViewById(R.id.dealers_button);
        btnDealers.setOnClickListener(this);

        btnBuildingMaps = (TextView) findViewById(R.id.building_maps_button);
        btnBuildingMaps.setOnClickListener(this);

        btnAreaMap = (TextView) findViewById(R.id.area_map_button);
        btnAreaMap.setOnClickListener(this);

        toggleButtons(false);

        // Handle fragments
        if (savedInstanceState != null) {
            mUpdateFragment = (UpdateFragment) getSupportFragmentManager().findFragmentByTag("Updater");
        }

        if (mUpdateFragment == null) {
            mUpdateFragment = new UpdateFragment();
            getSupportFragmentManager().beginTransaction().add(mUpdateFragment, "Updater").commit();
        }

        mUpdateFragment.setUpdaterEvent(onUpdaterEvent);

        // Drawer
        mDrawerLayout = (HackyDrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("AboutShown", mAboutShown);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;

        if (mUpdateFragment.getShouldUpdate()) {
            startListUpdate(false);
        } else {
            populateConList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_reload:
                // Catch it from the About menu inflate
                Log.v("MainActivity.onOptionsItemSelected selected reload menu option");
                startListUpdate(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_button:
                startFragment(new SchedulePagerFragment(), true);
                break;

            case R.id.guide_button:
                if (mDbInfo == null) {
                    return;
                }

                if ("0".equals(mDbInfo.get(DbLoader.INFO_HAS_GUIDE))) {
                    if (!"".equals(mDbInfo.get(DbLoader.INFO_GUIDE_URL))) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(mDbInfo.get(DbLoader.INFO_GUIDE_URL)));
                        startSafeActivity(i);
                    } else {
                        showError("This convention does not support the guide feature.", false);
                        return;
                    }
                } else {
                    startFragment(new GuidePagerFragment(), true);
                }
                break;

            case R.id.con_info_button:
                startFragment(new ConInfoListFragment(), true);
                break;

            case R.id.dealers_button:
                startFragment(new DealersListFragment(), true);
                break;

            case R.id.building_maps_button:
                startFragment(new BuildingMapListFragment(), true);
                break;

            case R.id.area_map_button:
                Intent i = new Intent(Intent.ACTION_VIEW);

                if (mDbInfo == null) {
                    return;
                }

                i.setData(Uri.parse(mDbInfo.get(DbLoader.INFO_AREA_MAP)));
                startSafeActivity(i);
                break;
        }
    }

    public void startFragment(Fragment fragment, boolean isRoot) {
        startFragment(fragment, isRoot, true);
    }

    public void startFragment(Fragment fragment, boolean isRoot, boolean closeDrawer) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);

        if (!isRoot) {
            transaction.addToBackStack("main");
        }

        transaction.commit();

        if (closeDrawer) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    public void startSafeActivity(Intent i) {
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            showError("Could not find an application to handle the request.", false);
        }
    }

    private void startListUpdate(boolean forceRefresh) {
        toggleButtons(false);
        mUpdateFragment.startListUpdate(forceRefresh);
    }

    private void startConUpdate(String conName) {
        toggleButtons(false);
        if (!conName.equals(mUpdateFragment.getConName())) {
            toggleButtons(false);
            mAboutShown = false;
            mUpdateFragment.startConUpdate(conName);
        } else {
            onUpdaterEvent.sendEmptyMessage(UpdateFragment.FINISH_CON_OK);
        }
    }

    private void showUpdateProgress(String message) {
        if (message != null) {
            tvUpdateMessage.setText(message);
            llUpdateMessage.setVisibility(View.VISIBLE);
        } else {
            llUpdateMessage.setVisibility(View.GONE);
        }
    }

    private void hideUpdateProgress() {
        llUpdateMessage.setVisibility(View.GONE);
    }

    private void toggleButtons(boolean state) {
        TextView[] buttons = {btnSchedule, btnGuide, btnConInfo, btnDealers, btnBuildingMaps, btnAreaMap};

        for (TextView button : buttons) {
            button.setEnabled(state);
        }
    }

    private void showError(String message, final boolean fatal) {
        Log.w("MainActivity.showError displaying error dialog (fatal: " + fatal + ") with message: " + message);

        if (mAlertShown) {
            Log.v("MainActivity.showError ignoring due to already being shown");
            return;
        }

        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mAlertShown = false;
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message).setCancelable(false).setOnCancelListener(cancelListener);

        if (fatal) {
            alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mAlertShown = false;

                    // Wipe existing data to force a full refresh
                    mUpdateFragment.mUpdateChecker = null;
                    startListUpdate(true);
                    dialog.dismiss();
                }
            });
        }

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mAlertShown = false;
                dialog.cancel();
                if (fatal) MainActivity.this.finish();
            }
        }).show();

        mAlertShown = true;
    }

    private void populateConList() {
        UpdateChecker updateChecker = mUpdateFragment.mUpdateChecker;

        if (updateChecker == null) {
            return;
        }

        UpdaterInfo.ConventionInfo cons[] = updateChecker.getCons();
        if (cons == null) {
            return;
        }

        String currentCon = updateChecker.getConName();

        int curPos = -1;
        for (int i = 0; i < cons.length; i++) {
            if (cons[i].path.equals(currentCon)) {
                curPos = i;
            }
        }

        if (curPos < 0) {
            // Old con was not found, default to first new one
            Log.d("MainActivity.populateConList could not find previous con " + currentCon + ", defaulting to " + cons[0].path);
            curPos = 0;
            currentCon = cons[0].path;
        }

        ArrayAdapter<UpdaterInfo.ConventionInfo> arrayAdapter = new ArrayAdapter<UpdaterInfo.ConventionInfo>(this, R.layout.spinner_con_item, updateChecker.getCons()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.spinner_con_item, parent, false);
                }

                UpdaterInfo.ConventionInfo info = getItem(position);

                ((TextView) view.findViewById(android.R.id.text1)).setText(info.name);
                ((TextView) view.findViewById(android.R.id.text2)).setText(info.details);

                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.spinner_con_dropdown_item, parent, false);
                }

                UpdaterInfo.ConventionInfo info = getItem(position);

                ((TextView) view.findViewById(android.R.id.text1)).setText(info.name);
                ((TextView) view.findViewById(android.R.id.text2)).setText(info.details);

                return view;
            }
        };

        arrayAdapter.setDropDownViewResource(R.layout.spinner_con_dropdown_item);

        spConSelector.setAdapter(arrayAdapter);
        spConSelector.setSelection(curPos, false);
        spConSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UpdaterInfo.ConventionInfo info = (UpdaterInfo.ConventionInfo) parent.getItemAtPosition(position);
                if (info == null) {
                    return;
                }

                Log.v("MainActivity.populateConList spConSelector.setOnItemSelectedListener.onItemSelected item " + position);

                startConUpdate(info.path);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        startConUpdate(currentCon);
    }

    private void refreshFromDb() {
        if (mUpdateFragment.isUpdating()) {
            return;
        }

        try {
            updateDbInfo();
            updateInfoDisplays();
        } catch (SQLiteException se) {
            mDbInfo = null;
        }

        if (mDbInfo == null) {
            Log.w("MainActivity.refreshFromDb dbInfo is still null");
            showError("The database could not be loaded.", true);
            return;
        } else {
            toggleButtons(true);
        }

        if (!mAboutShown && !mIsPaused) {
            startFragment(AboutFragment.createInstance(mDbInfo), true, false);
            mDrawerLayout.openDrawer(Gravity.LEFT);
            mAboutShown = true;
        }

        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Crashlytics.setString("ConName", mUpdateFragment.getConName());
        }
    }

    private void updateDbInfo() {
        SQLiteDatabase db = openOrCreateDatabase(DbLoader.DATABASE_NAME, 0, null);
        DbLoader loader = new DbLoader(db);
        mDbInfo = loader.getInfoCache();
        db.close();
    }

    private void updateInfoDisplays() {
        if (mDbInfo.containsKey(DbLoader.INFO_CONVENTION)) {
            getSupportActionBar().setTitle(mDbInfo.get(DbLoader.INFO_CONVENTION));
        } else {
            getSupportActionBar().setTitle("Error: Reload DB!");
        }
    }
}