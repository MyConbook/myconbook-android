package net.myconbook.android.updater;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import net.myconbook.android.DbLoader;
import net.myconbook.android.Log;

import java.io.File;

public class DbCheckState extends BaseState {
    public DbCheckState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        // Initialize DB if it doesn't exist
        boolean initDbState;
        try {
            initDbState = initDb();
        } catch (UpdaterAlertException e) {
            return error(e);
        }

        if (!initDbState || mGlobal.force) {
            // Force an update
            Log.v("USM:DbCheckState.run forcing DB upgrade");
            return State.DbUpdate;
        } else {
            // Check version then update
            int newDbVer = mGlobal.updateChecker.getDbVersion();
            int curDbVer = getCurrentDbVer();
            if (newDbVer > curDbVer) {
                Log.v("USM:DbCheckState.run upgrading database from " + curDbVer + " to " + newDbVer);
                return State.DbUpdate;
            }
        }

        return State.MapCheck;
    }

    private boolean initDb() throws UpdaterAlertException {
        Log.v("USM:DbCheckState.initDb checking existing database");

        File f = mGlobal.context.getDatabasePath(DbLoader.DATABASE_NAME);

        if (!f.exists()) {
            // Create
            Log.v("USM:DbCheckState.initDb creating blank database");

            try {
                mGlobal.context.openOrCreateDatabase(DbLoader.DATABASE_NAME, 0, null).close();
            } catch (SQLiteException e) {
                Log.e("USM:DbCheckState.initDb error creating blank database", e);
                Log.c(e);
                throw new UpdaterAlertException("Error creating database file. Restart the application and try again.");
            }

            return false;
        }

        return true;
    }

    private int getCurrentDbVer() {
        SQLiteDatabase db = mGlobal.context.openOrCreateDatabase(DbLoader.DATABASE_NAME, 0, null);
        DbLoader loader = new DbLoader(db);
        db.close();

        if (!loader.isValid()) {
            Log.w("USM:DbCheckState.getCurrentDbVer database not valid");
            return -1;
        }

        return loader.getDbVersion();
    }
}
