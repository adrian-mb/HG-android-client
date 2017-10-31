package hg.hg_android_client.mainscreen.repository;

import android.content.Context;

public class StateRepositoryFactory {

    public StateRepository get(Context context) {
        return new StateRepositoryImpl(context);
    }

}
