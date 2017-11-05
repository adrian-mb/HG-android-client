package hg.hg_android_client.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.mainscreen.event.FirebaseTokenUpdate;

public class TokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        EventBus.getDefault().post(new FirebaseTokenUpdate(token));
    }

}
