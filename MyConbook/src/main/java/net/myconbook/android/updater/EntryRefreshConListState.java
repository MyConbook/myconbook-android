package net.myconbook.android.updater;

public class EntryRefreshConListState extends BaseState {
    public EntryRefreshConListState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        mGlobal.updateListFlow = true;
        return State.AppUpgradeCheck;
    }
}
