package net.myconbook.android.updater;

public class UpdaterAlertException extends Exception {
    private static final long serialVersionUID = -339763661511521013L;

    public UpdaterAlertException(String message) {
        super(message);
    }

    public UpdaterAlertException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
