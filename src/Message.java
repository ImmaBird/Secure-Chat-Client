import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Message implements Serializable {

    public static enum messageType {
        text, picture, serverCommand, serverReply
    };

    private String senderName;
    private int senderId;
    private String text;
    private SendableImage image;
    private messageType type;

    public Message(String message, messageType type) {
        this.type = type;
        if (this.type == messageType.picture) {
            this.image = new SendableImage(message);
        } else {
            this.text = message;
        }
    }

    public void setSenderId(int num) {
        this.senderId = num;
    }

    public int getSenderId() {
        return this.senderId;
    }

    public messageType getType() {
        return this.type;
    }

    public String getText() {
        return text;
    }

    public void setSenderName(String name){
        this.senderName = name;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public BufferedImage getImage() {
        try {
            return this.image.fromByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}