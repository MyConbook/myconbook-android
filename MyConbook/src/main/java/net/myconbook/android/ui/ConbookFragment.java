package net.myconbook.android.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Andrew on 5/15/2014.
 */
public abstract class ConbookFragment extends Fragment {
    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public void startSafeActivity(Intent i) {
        getMainActivity().startSafeActivity(i);
    }
}
