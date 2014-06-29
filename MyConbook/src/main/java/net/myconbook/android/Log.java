package net.myconbook.android;

import com.crashlytics.android.Crashlytics;

/**
 * MyConbook logger.
 */
public class Log {
    private static final String TAG = "MyConbook";

    public static void v(String msg) {
        if (BuildConfig.DEBUG) android.util.Log.v(TAG, msg);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) android.util.Log.d(TAG, msg);
    }

    public static void w(String msg) {
        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Crashlytics.log(android.util.Log.WARN, TAG, msg);
        } else {
            android.util.Log.w(TAG, msg);
        }
    }

    public static void w(String msg, Throwable e) {
        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Crashlytics.log(android.util.Log.WARN, TAG, msg);
        } else {
            android.util.Log.w(TAG, msg, e);
        }
    }

    public static void e(String msg, Throwable e) {
        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Crashlytics.log(android.util.Log.ERROR, TAG, msg);
        } else {
            android.util.Log.e(TAG, msg, e);
        }
    }

    public static void c(Throwable e) {
        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Crashlytics.logException(e);
        }
    }
}
