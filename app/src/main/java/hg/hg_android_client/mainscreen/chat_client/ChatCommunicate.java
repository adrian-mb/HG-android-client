package hg.hg_android_client.mainscreen.chat_client;

import com.fasterxml.jackson.annotation.JsonIgnore;

final class ChatCommunicate {
    private boolean received;
    private ChatMessage message;

    public ChatCommunicate() {
    }

    private ChatCommunicate(ChatMessage message, boolean received) {
        this.message = message;
        this.received = received;
    }

    public static ChatCommunicate received(ChatMessage message) {
        return new ChatCommunicate(message, true);
    }

    public static ChatCommunicate sent(ChatMessage message) {
        return new ChatCommunicate(message, false);
    }

    public ChatMessage getMessage() {
        return message;
    }

    @JsonIgnore
    public String getTextMessage() {
        return message.getMessage();
    }

    @JsonIgnore
    public String getSender() {
        return message.getSenderName();
    }

    public boolean isReceived() {
        return received;
    }

}
