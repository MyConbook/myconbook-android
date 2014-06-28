package net.myconbook.android.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

public abstract class ConbookListFragment extends ListFragment {
    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public void startFragment(Fragment fragment) {
        getMainActivity().startFragment(fragment, false);
    }

    public void startSafeActivity(Intent i) {
        getMainActivity().startSafeActivity(i);
    }
}
