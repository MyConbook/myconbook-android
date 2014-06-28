package net.myconbook.android.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.myconbook.android.Log;
import net.myconbook.android.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewFragment extends ConbookFragment {
    public static ImageViewFragment createInstance(String file, String title) {
        ImageViewFragment fragment = new ImageViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("Filename", file);
        bundle.putString("Title", title);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.imageview_fragment, container, false);

        String title = getArguments().getString("Title");
        String filename = getArguments().getString("Filename");

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText(title);

        File dataDir = getActivity().getDir("maps", Context.MODE_PRIVATE);
        String url = "file://" + dataDir.getAbsolutePath() + "/" + filename + ".png";
        Log.v("ImageViewFragment.onCreateView loading image URL " + url);

        ImageView ivImage = (ImageView) view.findViewById(R.id.image);
        ivImage.setImageURI(Uri.parse(url));
        new PhotoViewAttacher(ivImage);

        return view;
    }
}
