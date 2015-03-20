package net.myconbook.android.model;

import java.util.List;

/**
 * Created by Andrew on 6/6/2014.
 */
public class UpdaterInfo {
    public UpdaterVersions versions;
    public List<ConventionInfo> cons;

    public static class UpdaterVersions {
        public int android;
    }

    public static class ConventionInfo {
        public String path;
        public String name;
        public String details;
    }
}
