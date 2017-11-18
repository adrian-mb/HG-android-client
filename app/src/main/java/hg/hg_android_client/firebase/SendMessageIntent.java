package hg.hg_android_client.firebase;

import android.content.Context;
import android.content.Intent;

import hg.hg_android_client.firebase.message.MessageType;
import hg.hg_android_client.util.JsonTransform;

public class SendMessageIntent extends Intent {

    private Intent wrapped;

    public static final String KEY_TARGET = "KEY_TARGET";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_PAYLOAD = "KEY_PAYLOAD";

    public SendMessageIntent(Intent wrapped) {
        this.wrapped = wrapped;
    }

    public SendMessageIntent(Context context, Long to, MessageType type, Object payload) {
        super(context, MessageSenderService.class);
        JsonTransform t = new JsonTransform();
        putExtra(KEY_TARGET, to);
        putExtra(KEY_TYPE, type.name());
        putExtra(KEY_PAYLOAD, t.toJson(payload));
    }

    public String getTarget() {
        return get(KEY_TARGET);
    }

    public String getType() {
        return get(KEY_TYPE);
    }

    public String getPayload() {
        return get(KEY_PAYLOAD);
    }

    private String get(String key) {
        if (wrapped == null) {
            return getStringExtra(key);
        } else {
            return wrapped.getStringExtra(key);
        }
    }

}
