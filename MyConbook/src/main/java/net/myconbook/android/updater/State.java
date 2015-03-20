package net.myconbook.android.updater;

public enum State {
    /**
     * Entry: Refresh con list
     */
    EntryRefreshConList,
    /**
     * Entry: Con selection change
     */
    EntryConChange,
    /**
     * App: Check for upgrade
     */
    AppUpgradeCheck,
    /**
     * List: Update
     */
    ListUpdate,
    /**
     * Con version: Update
     */
    ConVersionUpdate,
    /**
     * DB: Check for update
     */
    DbCheck,
    /**
     * DB: Update
     */
    DbUpdate,
    /**
     * Map: Check for update
     */
    MapCheck,
    /**
     * Map: Update
     */
    MapUpdate,
    /**
     * Update success
     */
    Success,
    /**
     * Exit: All cases
     */
    Exit
}
