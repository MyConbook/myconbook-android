package net.myconbook.android.updater;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.DbLoader;
import net.myconbook.android.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DbUpdateState extends BaseState {
    public DbUpdateState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        Log.v("USM:DbUpdateState.run downloading database from network for con: " + mGlobal.updateChecker.getConName());
        mGlobal.pushMessage("Downloading new database...");

        // Delete old
        mGlobal.context.deleteDatabase(DbLoader.DATABASE_NAME);

        // Download
        File f = mGlobal.context.getDatabasePath(DbLoader.DATABASE_NAME);
        OutputStream outStream;
        try {
            outStream = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            return error(new UpdaterAlertException("Error creating database file.", e));
        }

        try {
            fetchFile(outStream, BuildConfig.DATA_PATH + mGlobal.updateChecker.getConName() + "/conbook.sqlite");
        } catch (IOException e) {
            return error(new UpdaterAlertException("Error writing database to file.", e));
        }

        return State.MapCheck;
    }
}
