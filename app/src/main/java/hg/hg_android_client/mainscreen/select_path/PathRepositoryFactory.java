package hg.hg_android_client.mainscreen.select_path;

import android.content.Context;

public class PathRepositoryFactory {

    public PathRepository get(Context context) {
        return new GooglePathRepository(context);
    }

}
