package hg.hg_android_client.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import hg.hg_android_client.firebase.message.MessageType;
import hg.hg_android_client.mainscreen.chat_client.ChatMessage;
import hg.hg_android_client.mainscreen.chat_client.ChatMessageRepository;
import hg.hg_android_client.mainscreen.event.DeclineTripRequest;
import hg.hg_android_client.mainscreen.event.DriverTripConfirmation;
import hg.hg_android_client.mainscreen.event.ReceiveFinishTrip;
import hg.hg_android_client.mainscreen.event.ReceiveInCar;
import hg.hg_android_client.mainscreen.event.ReceivedCancelTrip;
import hg.hg_android_client.mainscreen.event.TripRequest;
import hg.hg_android_client.util.JsonTransform;

public class MessagingService extends FirebaseMessagingService {

    private static final String KEY_TYPE = "type";
    private static final String KEY_PAYLOAD = "payload";

    @Override
    public void onMessageReceived(RemoteMessage m) {
        // TODO: Actually send these to TripService to post,
        // or bring main screen activity up, or something... think cases.

        Map<String, String> data = m.getData();

        MessageType type = MessageType.valueOf(data.get(KEY_TYPE));
        String payload = data.get(KEY_PAYLOAD);

        switch(type) {
            case PASSENGER_SELECT_DRIVER:
                EventBus.getDefault().post(fromJson(payload, TripRequest.class));
                break;
            case DRIVER_ACCEPT_TRIP:
                EventBus.getDefault().post(fromJson(payload, DriverTripConfirmation.class));
                break;
            case DRIVER_DECLINE_TRIP:
                EventBus.getDefault().post(fromJson(payload, DeclineTripRequest.class));
                break;
            case CANCEL_TRIP:
                EventBus.getDefault().post(fromJson(payload, ReceivedCancelTrip.class));
                break;
            case IN_CAR:
                EventBus.getDefault().post(fromJson(payload, ReceiveInCar.class));
                break;
            case FINISH_TRIP:
                EventBus.getDefault().post(fromJson(payload, ReceiveFinishTrip.class));
                break;
            case CHAT_MESSAGE:
                ChatMessage message = fromJson(payload, ChatMessage.class);
                ChatMessageRepository r = new ChatMessageRepository(getApplicationContext());
                r.receive(message);
                EventBus.getDefault().post(message);
                break;
        }
    }

    private <T> T fromJson(String json, Class<T> type) {
        JsonTransform t = new JsonTransform();
        return t.fromJson(json, type);
    }

}
