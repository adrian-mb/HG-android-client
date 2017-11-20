package hg.hg_android_client.mainscreen.chat_client;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import hg.hg_android_client.R;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;

public class ChatDialog extends Dialog {

    public ChatDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        initializeButtonSend();
        initializeButtonClose();
        initializeChat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        scrollBottom();
    }

    private void initializeButtonClose() {
        ImageButton b = (ImageButton) findViewById(R.id.button_close);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void initializeButtonSend() {
        ImageButton b = (ImageButton) findViewById(R.id.button_send);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText m = (EditText) findViewById(R.id.message);
                String message = m.getText().toString();
                if (!"".equals(message.trim())) {
                    String sender = retrieveSender();
                    ChatMessage sent = new ChatMessage(sender, message);
                    sendMessage(sent);
                }
                m.setText("");
            }
        });
    }

    private String retrieveSender() {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository(getContext());
        return r.retrieveCached().getFullName();
    }

    private void sendMessage(ChatMessage message) {
        ChatMessageRepository r = new ChatMessageRepository(getContext());
        r.send(message, getContext());
        insertCommunicate(ChatCommunicate.sent(message));
    }

    private void initializeChat() {
        List<ChatCommunicate> communication = loadCommunication();
        for (ChatCommunicate comm : communication) {
            insertCommunicate(comm);
        }
    }

    private List<ChatCommunicate> loadCommunication() {
        ChatMessageRepository repository = new ChatMessageRepository(getContext());
        return repository.retrieveMessages();
    }

    private void insertCommunicate(ChatCommunicate communicate) {
        LinearLayout chatBody = (LinearLayout) findViewById(R.id.chat_body);
        View view;
        if (communicate.isReceived()) {
            view = getLayoutInflater().inflate(R.layout.message_received, chatBody, false);
        } else {
            view = getLayoutInflater().inflate(R.layout.message_sent, chatBody, false);
        }
        ((TextView) view.findViewById(R.id.sender)).setText(communicate.getSender());
        ((TextView) view.findViewById(R.id.message)).setText(communicate.getMessage());
        chatBody.addView(view);
        scrollBottom();
    }

    private void scrollBottom() {
        ScrollView scroll = (ScrollView) findViewById(R.id.scrollbar);
        scroll.scrollTo(0, scroll.getBottom());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessage(ChatMessage message) {
        insertCommunicate(ChatCommunicate.received(message));
    }

}
