package net.myconbook.android.updater;

import android.content.Context;

import net.myconbook.android.Log;

public class UpdateStateMachine {
    public InternalState run(EntryState entryState) {
        InternalState internalState = new InternalState(entryState);
        State currentState = entryState.entryState;
        BaseState currentStateClass;

        do {
            Log.v("USM:UpdateStateMachine.run switching to state " + currentState);
            currentStateClass = createState(currentState, internalState);
            if (currentStateClass == null) {
                break;
            }

            currentState = currentStateClass.run();
        } while (currentState != State.Exit);

        Log.v("USM:UpdateStateMachine.run done with execution");

        return internalState;
    }

    private BaseState createState(State state, InternalState internalState) {
        internalState.lastState = state;

        switch (state) {
            case EntryRefreshConList:
                return new EntryRefreshConListState(internalState);
            case EntryConChange:
                return new EntryConChangeState(internalState);
            case AppUpgradeCheck:
                return new AppUpgradeCheckState(internalState);
            case ListUpdate:
                return new ListUpdateState(internalState);
            case ConVersionUpdate:
                return new ConVersionUpdateState(internalState);
            case DbCheck:
                return new DbCheckState(internalState);
            case DbUpdate:
                return new DbUpdateState(internalState);
            case MapCheck:
                return new MapCheckState(internalState);
            case MapUpdate:
                return new MapUpdateState(internalState);
            case Success:
                return new SuccessState(internalState);
            default:
                return null;
        }
    }

    public static class EntryState {
        public State entryState;
        public Context context;
        public UpdateMessageListener messageListener;
        public String newCon;
        public UpdateChecker updateChecker;
        public boolean force;

        public EntryState(State state, Context context) {
            this.entryState = state;
            this.context = context;
        }
    }

    public static class InternalState {
        public Context context;
        public EntryState entryState;
        public State lastState;

        public UpdaterAlertException lastError;
        public boolean lastErrorWasAlert;

        public boolean updateListFlow;
        public UpdateChecker updateChecker;
        public boolean force;

        public InternalState(EntryState entryState) {
            this.context = entryState.context;
            this.entryState = entryState;
            this.updateChecker = entryState.updateChecker;
            this.force = entryState.force;
        }

        public void pushMessage(String message) {
            if (entryState.messageListener != null) {
                entryState.messageListener.onMessage(message);
            }
        }
    }

    public interface UpdateMessageListener {
        public void onMessage(String message);
    }
}
