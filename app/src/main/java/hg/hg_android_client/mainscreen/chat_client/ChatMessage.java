package hg.hg_android_client.mainscreen.chat_client;

public class ChatMessage {
    private String senderName;
    private String message;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String message) {
        this.message = message;
        this.senderName = sender;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
