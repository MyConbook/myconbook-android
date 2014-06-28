package net.myconbook.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.myconbook.android.DbLoader;
import net.myconbook.android.R;

import java.util.HashMap;

public class AboutFragment extends ConbookFragment {
    private HashMap<String, String> mDbInfo;

    public static AboutFragment createInstance(HashMap<String, String> info) {
        AboutFragment fragment = new AboutFragment();
        fragment.mDbInfo = info;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mDbInfo = (HashMap<String, String>) savedInstanceState.getSerializable("DbInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        TextView tvAboutInfo = (TextView) view.findViewById(R.id.tvAboutInfo);
        tvAboutInfo.setText("Support information\nDatabase build date:\n" + mDbInfo.get(DbLoader.INFO_BUILDDATE) + " (v " + mDbInfo.get(DbLoader.INFO_DBVER) + ")");

        TextView tvDataAttribution = (TextView) view.findViewById(R.id.tvDataAttribution);
        tvDataAttribution.setText(mDbInfo.get(DbLoader.INFO_PROVIDER_DETAILS));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("DbInfo", mDbInfo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_about, menu);
    }
}
