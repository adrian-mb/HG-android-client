package hg.hg_android_client.mainscreen.chat_client;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import hg.hg_android_client.firebase.SendMessageIntent;
import hg.hg_android_client.firebase.message.MessageType;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;
import hg.hg_android_client.util.JsonTransform;

public class ChatMessageRepository {

    private static final String KEY_CHAT = "KEY_CHAT";
    private static final String KEY_MESSAGE_LIST = "KEY_MESSAGE_LIST";

    private SharedPreferences preferences;

    public ChatMessageRepository(Context context) {
        this.preferences = context.getSharedPreferences(KEY_CHAT, Context.MODE_PRIVATE);
    }

    public void clean() {
        preferences.edit().remove(KEY_MESSAGE_LIST).commit();
    }

    public void receive(ChatMessage message) {
        List<ChatCommunicate> sequence = retrieveMessages();
        sequence.add(ChatCommunicate.received(message));
        persist(sequence);
    }

    public void send(ChatMessage message, Context context) {
        List<ChatCommunicate> sequence = retrieveMessages();
        sequence.add(ChatCommunicate.sent(message));
        persist(sequence);
        sendRemote(message, context);
    }

    private void sendRemote(ChatMessage message, Context context) {
        MessageType type = MessageType.CHAT_MESSAGE;
        Long to = retrieveMessageTarget(context);
        SendMessageIntent i = new SendMessageIntent(context, to, type, message);
        context.startService(i);
    }

    private Long retrieveMessageTarget(Context context) {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository(context);
        Profile p = r.retrieveCached();

        StateRepositoryFactory sf = new StateRepositoryFactory();
        StateRepository sr = sf.get(context);

        if (p.isPassenger()) {
            return sr.getDriver().getUserId();
        } else {
            return sr.getPassenger().getUserId();
        }
    }

    public List<ChatCommunicate> retrieveMessages() {
        String raw = preferences.getString(KEY_MESSAGE_LIST, "[]");
        JsonTransform t = new JsonTransform();
        return t.fromJson(raw, new TypeReference<List<ChatCommunicate>>(){});
    }

    private void persist(List<ChatCommunicate> sequence) {
        JsonTransform t = new JsonTransform();
        String json = t.toJson(sequence);
        preferences.edit().putString(KEY_MESSAGE_LIST, json).commit();
    }

}
