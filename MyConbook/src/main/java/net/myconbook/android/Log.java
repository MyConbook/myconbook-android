package net.myconbook.android;

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
        android.util.Log.w(TAG, msg);
    }

    public static void w(String msg, Throwable e) {
        android.util.Log.w(TAG, msg, e);
    }

    public static void e(String msg, Throwable e) {
        android.util.Log.e(TAG, msg, e);
    }
}
