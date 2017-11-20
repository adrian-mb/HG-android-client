package hg.hg_android_client.mainscreen.chat_client;

final class ChatCommunicate {
    private boolean received;
    private ChatMessage message;

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

    public String getMessage() {
        return message.getMessage();
    }

    public String getSender() {
        return message.getSenderName();
    }

    public boolean isReceived() {
        return received;
    }

}
