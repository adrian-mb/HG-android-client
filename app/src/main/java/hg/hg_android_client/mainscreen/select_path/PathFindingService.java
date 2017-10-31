package hg.hg_android_client.mainscreen.select_path;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.event.PathResponse;

public class PathFindingService extends IntentService {

    private static final String SERVICE_NAME = "PathFindingService";

    public PathFindingService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String token = retrieveCachedToken();

        PathQueryIntent i = new PathQueryIntent(intent);
        LatLng origin = i.getOrigin();
        LatLng destination = i.getDestination();

        PathRepository repository = new PathRepository(getApplicationContext());
        List<Path> retrieved = repository.query(token, origin, destination);

        if (retrieved != null) {
            PathResponse r = new PathResponse(retrieved);
            EventBus.getDefault().post(r);
        }
    }

    private String retrieveCachedToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getApplicationContext());
        return r.getToken();
    }

}
