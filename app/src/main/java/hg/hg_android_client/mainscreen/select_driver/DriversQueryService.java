package hg.hg_android_client.mainscreen.select_driver;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.EventLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.event.DriversAroundResponse;
import hg.hg_android_client.mainscreen.select_path.Location;

public class DriversQueryService extends IntentService {

    private static final String SERVICE_NAME = "DriversQueryService";

    public DriversQueryService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String token = retrieveCachedToken();

        QueryDriversIntent i = new QueryDriversIntent(intent);
        Location location = i.getLocation();

        DriversAroundRepository repository = new DriversAroundRepository(getApplicationContext());
        List<Driver> retrieved = repository.query(token, location);

        if (retrieved != null) {
            DriversAroundResponse event = new DriversAroundResponse(retrieved);
            EventBus.getDefault().post(event);
        }
    }

    private String retrieveCachedToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getApplicationContext());
        return r.getToken();
    }

}
