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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
import net.myconbook.android.model.UpdaterInfo;

import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity implements OnClickListener {
    private HashMap<String, String> mDbInfo;
    private boolean mIsPaused;
    private boolean mAboutShown;
    private boolean mAlertShown;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
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
    private TextView btnAbout;

    private UpdateFragment mUpdateFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Fabric.with(this, new Crashlytics());
        }

        setContentView(R.layout.main_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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

        btnAbout = (TextView) findViewById(R.id.about_button);
        btnAbout.setOnClickListener(this);

        toggleButtons(false);

        // Handle fragments
        if (savedInstanceState != null) {
            mUpdateFragment = (UpdateFragment) getSupportFragmentManager().findFragmentByTag("Updater");
        }

        if (mUpdateFragment == null) {
            mUpdateFragment = new UpdateFragment();
            getSupportFragmentManager().beginTransaction().add(mUpdateFragment, "Updater").commit();
        }

        mUpdateFragment.setUpdateListener(mUpdateListener);

        // Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
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

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.material_blue_grey_700));

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

        if (UpdateFragment.getShouldUpdate(this)) {
            startListUpdate(false);
        } else {
            populateConList(mUpdateFragment.getUpdateChecker().getCons());
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
                        showError(getString(R.string.con_no_guide_feature), false);
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

            case R.id.about_button:
                startFragment(AboutFragment.createInstance(mDbInfo), true);
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
            showError(getString(R.string.activity_not_found), false);
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
            refreshFromDb();
        }
    }

    private void toggleButtons(boolean state) {
        TextView[] buttons = {btnSchedule, btnGuide, btnConInfo, btnDealers, btnBuildingMaps, btnAreaMap, btnAbout};

        for (TextView button : buttons) {
            button.setEnabled(state);
        }

        if (state) {
            llUpdateMessage.setVisibility(View.GONE);
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

        if (!fatal) {
            alert.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mAlertShown = false;
                    startListUpdate(true);
                    dialog.dismiss();
                }
            });
        }

        alert.setNegativeButton(fatal ? R.string.exit : android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mAlertShown = false;
                dialog.cancel();
                if (fatal) MainActivity.this.finish();
            }
        }).show();

        mAlertShown = true;
    }

    private void populateConList(List<UpdaterInfo.ConventionInfo> cons) {
        if (cons == null || cons.isEmpty()) {
            showError(getString(R.string.no_conventions_listed), true);
            return;
        }

        String currentCon = mUpdateFragment.getUpdateChecker().getConName();

        int curPos = -1;
        for (int i = 0; i < cons.size(); i++) {
            if (cons.get(i).path.equals(currentCon)) {
                curPos = i;
            }
        }

        if (curPos < 0) {
            // Old con was not found, default to first new one
            String path = mUpdateFragment.getUpdateChecker().getFirstCon();
            Log.d("MainActivity.populateConList could not find previous con " + currentCon + ", defaulting to " + path);
            curPos = 0;
            currentCon = path;
        }

        ArrayAdapter<UpdaterInfo.ConventionInfo> arrayAdapter = new ArrayAdapter<UpdaterInfo.ConventionInfo>(this, R.layout.spinner_con_item, cons) {
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
            showError(getString(R.string.error_database_not_loaded), true);
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
            Crashlytics.setInt("DbVer", mUpdateFragment.getUpdateChecker().getDbVersion());
            Crashlytics.setInt("MapVer", mUpdateFragment.getUpdateChecker().getMapVersion());
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
            getSupportActionBar().setTitle(getString(R.string.error_reload_db));
        }
    }

    private UpdateFragment.UpdaterListener mUpdateListener = new UpdateFragment.UpdaterListener() {
        @Override
        public void onNewerApp() {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            alert.setMessage(R.string.new_version_available)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
                            startSafeActivity(i);
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }

        @Override
        public void onListUpdate(List<UpdaterInfo.ConventionInfo> cons) {
            populateConList(cons);
        }

        @Override
        public void onConUpdate() {
            llUpdateMessage.setVisibility(View.GONE);
            refreshFromDb();
        }

        @Override
        public void onMessage(String message) {
            tvUpdateMessage.setText(message);
            llUpdateMessage.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAlert(String message, boolean fatal) {
            llUpdateMessage.setVisibility(View.GONE);
            showError(message, fatal);
        }

        @Override
        public void onNop() {
            llUpdateMessage.setVisibility(View.GONE);
        }
    };
}